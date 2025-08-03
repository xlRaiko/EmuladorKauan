/*
 * Decompiled with CFR 0.152.
 */
package io.netty.resolver;

import io.netty.resolver.HostsFileEntries;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public final class HostsFileParser {
    private static final String WINDOWS_DEFAULT_SYSTEM_ROOT = "C:\\Windows";
    private static final String WINDOWS_HOSTS_FILE_RELATIVE_PATH = "\\system32\\drivers\\etc\\hosts";
    private static final String X_PLATFORMS_HOSTS_FILE_PATH = "/etc/hosts";
    private static final Pattern WHITESPACES = Pattern.compile("[ \t]+");
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HostsFileParser.class);

    private static File locateHostsFile() {
        File hostsFile;
        if (PlatformDependent.isWindows()) {
            hostsFile = new File(System.getenv("SystemRoot") + WINDOWS_HOSTS_FILE_RELATIVE_PATH);
            if (!hostsFile.exists()) {
                hostsFile = new File("C:\\Windows\\system32\\drivers\\etc\\hosts");
            }
        } else {
            hostsFile = new File(X_PLATFORMS_HOSTS_FILE_PATH);
        }
        return hostsFile;
    }

    public static HostsFileEntries parseSilently() {
        return HostsFileParser.parseSilently(Charset.defaultCharset());
    }

    public static HostsFileEntries parseSilently(Charset ... charsets) {
        File hostsFile = HostsFileParser.locateHostsFile();
        try {
            return HostsFileParser.parse(hostsFile, charsets);
        }
        catch (IOException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to load and parse hosts file at " + hostsFile.getPath(), e);
            }
            return HostsFileEntries.EMPTY;
        }
    }

    public static HostsFileEntries parse() throws IOException {
        return HostsFileParser.parse(HostsFileParser.locateHostsFile());
    }

    public static HostsFileEntries parse(File file) throws IOException {
        return HostsFileParser.parse(file, Charset.defaultCharset());
    }

    public static HostsFileEntries parse(File file, Charset ... charsets) throws IOException {
        ObjectUtil.checkNotNull(file, "file");
        ObjectUtil.checkNotNull(charsets, "charsets");
        if (file.exists() && file.isFile()) {
            for (Charset charset : charsets) {
                HostsFileEntries entries = HostsFileParser.parse(new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), charset)));
                if (entries == HostsFileEntries.EMPTY) continue;
                return entries;
            }
        }
        return HostsFileEntries.EMPTY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static HostsFileEntries parse(Reader reader) throws IOException {
        ObjectUtil.checkNotNull(reader, "reader");
        BufferedReader buff = new BufferedReader(reader);
        try {
            String line;
            HashMap<String, Inet4Address> ipv4Entries = new HashMap<String, Inet4Address>();
            HashMap<String, Inet6Address> ipv6Entries = new HashMap<String, Inet6Address>();
            while ((line = buff.readLine()) != null) {
                byte[] ipBytes;
                int commentPosition = line.indexOf(35);
                if (commentPosition != -1) {
                    line = line.substring(0, commentPosition);
                }
                if ((line = line.trim()).isEmpty()) continue;
                ArrayList<String> lineParts = new ArrayList<String>();
                for (String s : WHITESPACES.split(line)) {
                    if (s.isEmpty()) continue;
                    lineParts.add(s);
                }
                if (lineParts.size() < 2 || (ipBytes = NetUtil.createByteArrayFromIpAddressString((String)lineParts.get(0))) == null) continue;
                for (int i = 1; i < lineParts.size(); ++i) {
                    InetAddress previous;
                    String hostname = (String)lineParts.get(i);
                    String hostnameLower = hostname.toLowerCase(Locale.ENGLISH);
                    InetAddress address = InetAddress.getByAddress(hostname, ipBytes);
                    if (address instanceof Inet4Address) {
                        previous = ipv4Entries.put(hostnameLower, (Inet4Address)address);
                        if (previous == null) continue;
                        ipv4Entries.put(hostnameLower, (Inet4Address)previous);
                        continue;
                    }
                    previous = ipv6Entries.put(hostnameLower, (Inet6Address)address);
                    if (previous == null) continue;
                    ipv6Entries.put(hostnameLower, (Inet6Address)previous);
                }
            }
            HostsFileEntries hostsFileEntries = ipv4Entries.isEmpty() && ipv6Entries.isEmpty() ? HostsFileEntries.EMPTY : new HostsFileEntries(ipv4Entries, ipv6Entries);
            return hostsFileEntries;
        }
        finally {
            try {
                buff.close();
            }
            catch (IOException e) {
                logger.warn("Failed to close a reader", e);
            }
        }
    }

    private HostsFileParser() {
    }
}

