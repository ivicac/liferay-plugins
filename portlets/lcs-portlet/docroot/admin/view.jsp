<%@ page import="com.liferay.lcs.util.HandshakeManagerUtil" %>

<%--
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
--%>

<%@ include file="../init.jsp" %>

<liferay-portlet:actionURL name="update" var="updateURL" />
<liferay-portlet:actionURL name="start" var="startURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL name="stop" var="stopURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</liferay-portlet:actionURL>

<label>Start/Stop Gateway</label>
<br />
<label>Start Gateway</label>

<c:if test="<%= HandshakeManagerUtil.isPending() || HandshakeManagerUtil.isReady() %>">

	Start

</c:if>
<c:if test="<%= !HandshakeManagerUtil.isPending() && !HandshakeManagerUtil.isReady() %>">

	<a href="<%= startURL %>" name="start" style="">Start</a>

</c:if>

</br>
<label>Stop Gateway</label>

<c:if test="<%= HandshakeManagerUtil.isPending() || !HandshakeManagerUtil.isReady() %>">

	Stop

</c:if>
<c:if test="<%= !HandshakeManagerUtil.isPending() && HandshakeManagerUtil.isReady() %>">

	<a href="<%= stopURL %>" name="stop">Stop</a>

</c:if>

<br /><br />
<div class="portlet-msg-info">
	Communication with Liferay CS is <%= HandshakeManagerUtil.isReady() == true ? "" : "not" %> ready
</div>