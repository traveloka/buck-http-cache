package com.uber.buckcache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ignite.cache.CacheMode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IgniteConfig {

  @Nonnull
  private final String multicastIP;
  @Nonnull
  private final Integer multicastPort;
  @Nonnull
  private final CacheMode cacheMode;
  @Nonnull
  private final Integer cacheBackupCount;
  @Nonnull
  private final TimeUnit expirationTimeUnit;
  @Nonnull
  private final Long expirationTimeValue;
  @Nonnull
  private final Integer atomicSequenceReserveSize;
  @Nonnull
  private final String memoryStorageSize;
  @Nonnull
  private final List<String> hostIPs;
  @Nonnull
  private final String dnsLookupAddress;

  @JsonCreator
  public IgniteConfig(
      @Nonnull @JsonProperty("multicastIP") String multicastIP,
      @Nonnull @JsonProperty("multicastPort") Integer multicastPort,
      @Nonnull @JsonProperty("cacheMode") CacheMode cacheMode,
      @Nonnull @JsonProperty("cacheBackupCount") Integer cacheBackupCount,
      @Nonnull @JsonProperty("expirationTimeUnit") TimeUnit expirationTimeUnit,
      @Nonnull @JsonProperty("expirationTimeValue") Long expirationTimeValue,
      @Nonnull @JsonProperty("atomicSequenceReserveSize") Integer atomicSequenceReserveSize,
      @Nonnull @JsonProperty("memoryStorageSize") String memoryStorageSize,
      @Nonnull @JsonProperty("hostIPs") List<String> hostIPs,
      @Nonnull @JsonProperty("dnsLookupAddress") String dnsLookupAddress) {
    this.multicastIP = multicastIP;
    this.multicastPort = multicastPort;
    this.cacheMode = cacheMode;
    this.cacheBackupCount = cacheBackupCount;
    this.expirationTimeUnit = expirationTimeUnit;
    this.expirationTimeValue = expirationTimeValue;
    this.atomicSequenceReserveSize = atomicSequenceReserveSize;
    this.memoryStorageSize = memoryStorageSize;
    this.hostIPs = hostIPs;
    this.dnsLookupAddress = dnsLookupAddress;
  }

  public List<String> getHostIPs() {
    return hostIPs;
  }

  public String getMemoryStorageSize() {
    return memoryStorageSize;
  }

  public String getMulticastIP() {
    return multicastIP;
  }

  public Integer getMulticastPort() {
    return multicastPort;
  }

  public CacheMode getCacheMode() {
    return cacheMode;
  }

  public Integer getCacheBackupCount() {
    return cacheBackupCount;
  }

  public TimeUnit getExpirationTimeUnit() {
    return expirationTimeUnit;
  }

  public Long getExpirationTimeValue() {
    return expirationTimeValue;
  }

  public Integer getAtomicSequenceReserveSize() {
    return atomicSequenceReserveSize;
  }

  public String getDnsLookupAddress() {
    return dnsLookupAddress;
  }
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
