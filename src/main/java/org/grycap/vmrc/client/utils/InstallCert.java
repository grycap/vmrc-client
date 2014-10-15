package org.grycap.vmrc.client.utils;

/*
 * Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;

import java.security.*;
import java.security.cert.*;

import javax.net.ssl.*;

public class InstallCert {
	
  private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

  private static class SavingTrustManager implements X509TrustManager {
    private final X509TrustManager tm;
	private X509Certificate[] chain;

	SavingTrustManager(X509TrustManager tm) {
	  this.tm = tm;
	}

	public X509Certificate[] getAcceptedIssuers() {
	  throw new UnsupportedOperationException();
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
	  throws CertificateException {
	  throw new UnsupportedOperationException();
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
  	  throws CertificateException {
	  this.chain = chain;
	  tm.checkServerTrusted(chain, authType);
	}
  }

  private static String toHexString(byte[] bytes) {
	StringBuilder sb = new StringBuilder(bytes.length * 3);
	for (int b : bytes) {
      b &= 0xff;
	  sb.append(HEXDIGITS[b >> 4]);
	  sb.append(HEXDIGITS[b & 15]);
	  sb.append(' ');
	}
  	  return sb.toString();
  }

  /**
   * Get and store the SSL certificate from the tomcat server.
   * @param serviceURI the vmrc service address.
   * @return the exit code. 
   */
   public static String getServerCertificate(String host, int port, String certPath) throws Exception {
	char[] passphrase;

	passphrase = new String("changeit").toCharArray();

	KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	ks.load(null, passphrase);

	SSLContext context = SSLContext.getInstance("TLS");
	TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	tmf.init(ks);
	X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
	SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
	context.init(null, new TrustManager[] {tm}, null);
	SSLSocketFactory factory = context.getSocketFactory();

	SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
	socket.setSoTimeout(10000);
	try {
      socket.startHandshake();
	  socket.close();
	} catch (SSLException e) { }

	X509Certificate[] chain = tm.chain;
	if (chain == null) {
	  String msg = "Error getting vmrc service certificate. It's not possible to obtain the chain certificate. https://" + host + ":" + port;
	  System.out.println(msg);	
	  return msg;
	}

	X509Certificate cert = chain[0];
	String alias = host + "-" + 1;
	ks.setCertificateEntry(alias, cert);

	OutputStream out = new FileOutputStream(certPath);
	ks.store(out, passphrase);
	out.close();
	
	return "OK";
  }
	
}
