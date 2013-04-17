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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@
	page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
	page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
	page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
	page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
	page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
	page import="com.liferay.portal.kernel.util.StringBundler" %><%@
	page import="com.liferay.portal.kernel.util.StringPool" %><%@
	page import="com.liferay.portal.kernel.util.StringUtil" %><%@
	page import="com.liferay.portal.kernel.util.Validator" %><%@
	page import="com.liferay.lcs.util.Constants" %><%@
	page import="com.liferay.lcs.util.WebKeys" %><%@
	page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
	page import="com.liferay.portal.security.permission.ActionKeys" %><%@
	page import="com.liferay.portal.service.permission.PortletPermissionUtil" %><%@
	page import="com.liferay.portal.util.PortalUtil" %><%@
	page import="com.liferay.portal.util.PortletKeys" %><%@
	page import="com.liferay.portlet.PortletPreferencesFactoryUtil" %><%@
	page import="com.liferay.lcs.messaging.CommandMessage" %><%@
	page import="com.liferay.portlet.PortletURLUtil" %><%@
	page import="com.liferay.lcs.messaging.MetricsMessage" %><%@
	page import="com.liferay.lcs.util.HandshakeManagerUtil" %>
<%@
	page import="javax.portlet.PortletPreferences" %><%@
	page import="javax.portlet.WindowState" %><%@
	page import="java.util.List" %>

<portlet:defineObjects />

<liferay-theme:defineObjects />

<%
	String currentURL = PortletURLUtil.getCurrent(renderRequest, renderResponse).toString();
%>