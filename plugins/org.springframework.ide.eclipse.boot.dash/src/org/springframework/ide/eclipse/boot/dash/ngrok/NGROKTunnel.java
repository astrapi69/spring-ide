/*******************************************************************************
 * Copyright (c) 2015 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.ngrok;

/**
 * @author mlippert
 *
 */
public class NGROKTunnel {

	private String name;
	private String proto;
	private String public_url;
	private String addr;

	public NGROKTunnel(String name, String proto, String public_url, String addr) {
		super();
		this.name = name;
		this.proto = proto;
		this.public_url = public_url;
		this.addr = addr;
	}

	public String getName() {
		return name;
	}

	public String getProto() {
		return proto;
	}

	public String getPublic_url() {
		return public_url;
	}

	public String getAddr() {
		return addr;
	}

	@Override
	public String toString() {
		return "NGROKTunnel [name=" + name + ", proto=" + proto + ", public_url=" + public_url + ", addr=" + addr + "]";
	}

}
