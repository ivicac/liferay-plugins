/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.lcs.model;

/**
 * @author Igor Beslic
 */
public class LCSClusterEntry {

	public static final int LCS_CLUSTER_ENTRY_TYPE_CLUSTER = 0;

	public static final int LCS_CLUSTER_ENTRY_TYPE_ENVIRONMENT = 1;

	public long getCorpEntryId() {
		return _corpEntryId;
	}

	public String getCorpEntryName() {
		return _corpEntryName;
	}

	public String getDescription() {
		return _description;
	}

	public long getLcsClusterEntryId() {
		return _lcsClusterEntryId;
	}

	public String getLocation() {
		return _location;
	}

	public String getName() {
		return _name;
	}

	public int getType() {
		return _type;
	}

	public void setCorpEntryId(long corpEntryId) {
		_corpEntryId = corpEntryId;
	}

	public void setCorpEntryName(String corpEntryName) {
		_corpEntryName = corpEntryName;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public void setLcsClusterEntryId(long _lcsClusterEntryId) {
		this._lcsClusterEntryId = _lcsClusterEntryId;
	}

	public void setLocation(String location) {
		this._location = location;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setType(int type) {
		this._type = type;
	}

	private long _corpEntryId;
	private String _corpEntryName;
	private String _description;
	private long _lcsClusterEntryId;
	private String _location;
	private String _name;
	private int _type;
}