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

package com.liferay.oauth.model.impl;

import com.liferay.oauth.model.Application;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Application in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see Application
 * @generated
 */
public class ApplicationCacheModel implements CacheModel<Application>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(29);

		sb.append("{applicationId=");
		sb.append(applicationId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", website=");
		sb.append(website);
		sb.append(", callBackURL=");
		sb.append(callBackURL);
		sb.append(", accessLevel=");
		sb.append(accessLevel);
		sb.append(", consumerKey=");
		sb.append(consumerKey);
		sb.append(", consumerSecret=");
		sb.append(consumerSecret);
		sb.append(", logoId=");
		sb.append(logoId);
		sb.append("}");

		return sb.toString();
	}

	public Application toEntityModel() {
		ApplicationImpl applicationImpl = new ApplicationImpl();

		applicationImpl.setApplicationId(applicationId);
		applicationImpl.setCompanyId(companyId);
		applicationImpl.setUserId(userId);

		if (userName == null) {
			applicationImpl.setUserName(StringPool.BLANK);
		}
		else {
			applicationImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			applicationImpl.setCreateDate(null);
		}
		else {
			applicationImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			applicationImpl.setModifiedDate(null);
		}
		else {
			applicationImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			applicationImpl.setName(StringPool.BLANK);
		}
		else {
			applicationImpl.setName(name);
		}

		if (description == null) {
			applicationImpl.setDescription(StringPool.BLANK);
		}
		else {
			applicationImpl.setDescription(description);
		}

		if (website == null) {
			applicationImpl.setWebsite(StringPool.BLANK);
		}
		else {
			applicationImpl.setWebsite(website);
		}

		if (callBackURL == null) {
			applicationImpl.setCallBackURL(StringPool.BLANK);
		}
		else {
			applicationImpl.setCallBackURL(callBackURL);
		}

		applicationImpl.setAccessLevel(accessLevel);

		if (consumerKey == null) {
			applicationImpl.setConsumerKey(StringPool.BLANK);
		}
		else {
			applicationImpl.setConsumerKey(consumerKey);
		}

		if (consumerSecret == null) {
			applicationImpl.setConsumerSecret(StringPool.BLANK);
		}
		else {
			applicationImpl.setConsumerSecret(consumerSecret);
		}

		applicationImpl.setLogoId(logoId);

		applicationImpl.resetOriginalValues();

		return applicationImpl;
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		applicationId = objectInput.readLong();
		companyId = objectInput.readLong();
		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();
		description = objectInput.readUTF();
		website = objectInput.readUTF();
		callBackURL = objectInput.readUTF();
		accessLevel = objectInput.readInt();
		consumerKey = objectInput.readUTF();
		consumerSecret = objectInput.readUTF();
		logoId = objectInput.readLong();
	}

	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(applicationId);
		objectOutput.writeLong(companyId);
		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (description == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(description);
		}

		if (website == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(website);
		}

		if (callBackURL == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(callBackURL);
		}

		objectOutput.writeInt(accessLevel);

		if (consumerKey == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(consumerKey);
		}

		if (consumerSecret == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(consumerSecret);
		}

		objectOutput.writeLong(logoId);
	}

	public long applicationId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public String description;
	public String website;
	public String callBackURL;
	public int accessLevel;
	public String consumerKey;
	public String consumerSecret;
	public long logoId;
}