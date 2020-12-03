package com.uber.buckcache.datastore.impl.ignite;

import com.google.common.base.Strings;
import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.spotify.dns.LookupResult;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.AtomicConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IgniteConfigurationBuilder {
  private IgniteConfiguration igniteConfiguration;
  private final DnsSrvResolver dnsResolver;

  public IgniteConfigurationBuilder() {
    this(DnsSrvResolvers.newBuilder()
        .retainingDataOnFailures(true)
        .build());
    igniteConfiguration = new IgniteConfiguration();
  }

  protected IgniteConfigurationBuilder(DnsSrvResolver dnsResolver) {
    this.dnsResolver = dnsResolver;
    igniteConfiguration = new IgniteConfiguration();
  }

  public IgniteConfigurationBuilder addMulticastBasedDiscrovery(String multicastIP, Integer multicastPort, List<String> hostIPs, String dnsLookupAddress) {
    if (hostIPs == null) {
      hostIPs = new ArrayList<String>();
    }

    TcpDiscoverySpi spi = new TcpDiscoverySpi();

    TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
    ((TcpDiscoveryMulticastIpFinder) ipFinder).setMulticastGroup(multicastIP);
    ((TcpDiscoveryMulticastIpFinder) ipFinder).setMulticastPort(multicastPort);

    boolean hasDNSLookupAddress = !Strings.isNullOrEmpty(dnsLookupAddress);

    if (hasDNSLookupAddress) {
      hostIPs.addAll(this.resolveAddressByDNS(dnsLookupAddress));
    }
    ((TcpDiscoveryMulticastIpFinder) ipFinder).setAddresses(hostIPs);
    spi.setIpFinder(ipFinder);

    // Override default discovery SPI.
    igniteConfiguration.setDiscoverySpi(spi);
    return this;
  }

  public IgniteConfigurationBuilder addCacheConfiguration(CacheMode cacheMode, Integer backupCount,
      TimeUnit expirationTimeUnit, Long expirationTimeValue, String memoryStorageSize, String ...caches) {
    
    CacheConfiguration[] cacheConfigs = new CacheConfiguration[caches.length];
    
    for (int i = 0; i < caches.length; i++) {
      CacheConfiguration cacheConfiguration = new CacheConfiguration(caches[i]);
      cacheConfiguration.setCacheMode(cacheMode);
      cacheConfiguration.setStatisticsEnabled(true);
      cacheConfiguration.setBackups(backupCount);
      cacheConfiguration
          .setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(expirationTimeUnit, expirationTimeValue)));
      cacheConfigs[i] = cacheConfiguration;
    }

    // Set max memory storage size
    DataStorageConfiguration storageCfg = new DataStorageConfiguration();

    // You can set page size here. However, page size must not exceed the size of HDD/SDD block size. Default will be 4KB
    // In AWS, this is example of the block size for each EBS volume size. https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/volume_constraints.html
    storageCfg.getDefaultDataRegionConfiguration()
            .setPersistenceEnabled(true);
    storageCfg.setStoragePath("./storage");
    storageCfg.setWriteThrottlingEnabled(true);
    
    igniteConfiguration.setCacheConfiguration(cacheConfigs);
    igniteConfiguration.setDataStorageConfiguration(storageCfg);

    return this;
  }

  public IgniteConfigurationBuilder addAtomicSequenceConfig(Integer reserveSize) {
    AtomicConfiguration atomicCfg = new AtomicConfiguration();
    atomicCfg.setAtomicSequenceReserveSize(reserveSize);
    igniteConfiguration.setAtomicConfiguration(atomicCfg);
    return this;
  }

  public IgniteConfiguration build() {
    return igniteConfiguration;
  }

  private List<String> resolveAddressByDNS(String dnsLookupAddress) {
     List<String> address = new ArrayList<String>();
     for (LookupResult node : dnsResolver.resolve(dnsLookupAddress)) {
       address.add(node.host());
     }
     return address;
  }
}
