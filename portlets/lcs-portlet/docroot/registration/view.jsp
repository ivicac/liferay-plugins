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


<%
	boolean registered = (Boolean)renderRequest.getAttribute(WebKeys.REGISTERED);
	boolean pending = (Boolean)renderRequest.getAttribute(WebKeys.PENDING);
%>

<div class="portlet-msg-info <%= (registered == true && pending == false) ? "" : "aui-helper-hidden" %>" id="<portlet:namespace />registered">
	This Liferay instance is registered and synchronized with Liferay Cloud Services.
</div>

<div class="portlet-msg-info <%= pending == true ? "" : "aui-helper-hidden" %>" id="<portlet:namespace />pending">
	This Liferay instance is synchronizing with Liferay Cloud Services. Refresh page to see changes.
</div>

<div class="portlet-msg-error aui-helper-hidden">
	<liferay-ui:message key="your-request-failed-to-complete" />
</div>

<aui:form cssClass='<%= pending == false && registered == false ? "" : "aui-helper-hidden" %>' name="loginFm">
	<aui:fieldset>
		<aui:input name="userName">
		 	<aui:validator name="required" />
		 	<aui:validator name="email" />
		</aui:input>
		<aui:input name="password" type="passsword">
	 		<aui:validator name="required" />
	 	</aui:input>
		<aui:button-row>
			<aui:button cssClass="login" type="submit" value="login" />
		</aui:button-row>
	</aui:fieldset>
</aui:form>
<aui:form cssClass="aui-helper-hidden" name="addFm">
	<aui:fieldset>
		<aui:select name="account" />
		<aui:select name="lcsClusterEntry" />
		<aui:input name="lcsClusterEntryName" />
		<aui:input name="description" />
		<aui:input name="location" />
		<aui:button-row>
			<aui:button cssClass="add" type="submit" value="add" />
		</aui:button-row>
	</aui:fieldset>
</aui:form>

<aui:script use="aui-base,aui-io-request,aui-loading-mask,aui-form-validator,aui-overlay-context-panel">
	var loginButton = A.one('.login');
	var addButton = A.one('.add');
	var accountSelect = A.one('#<portlet:namespace />account');
	var lcsClusterEntrySelect = A.one('#<portlet:namespace />lcsClusterEntry');
	var lcsClusterEntryNameInput = A.one('#<portlet:namespace />lcsClusterEntryName');
	var loginForm = A.one('#<portlet:namespace />loginFm');
	var addForm = A.one('#<portlet:namespace />addFm');
	var pendingMsg = A.one('#<portlet:namespace />pending');
	var errorMsg = A.one('.portlet-msg-error');

	loginButton.on(
		'click',
		function(event) {
			event.preventDefault();

			A.io.request('<portlet:resourceURL id="login"><portlet:param name="<%= Constants.CMD %>" value="login" /></portlet:resourceURL>',
				{
					dataType: 'json',
					form: {
						id: loginForm.getDOM()
					},
					on: {
						success: function(event, id, obj) {
							var data = this.get('responseData');

							if(data.result == 'success'){
								loginForm.hide();
								addForm.show();

								<portlet:namespace />refreshForm(data);
							}else if(data.result == 'failure'){
								if (data.message) {
									errorMsg.html(data.message);
								}

								errorMsg.show();
							}
						}
					}
				}
			);
	    }
	);

	addButton.on(
		'click',
		function(event) {
			event.preventDefault();


			errorMsg.hide();

			A.io.request('<portlet:resourceURL id="add"><portlet:param name="<%= Constants.CMD %>" value="add" /></portlet:resourceURL>',
				{
					dataType: 'json',
					form: {
						id: addForm.getDOM()
					},
					on: {
						success: function(event, id, obj) {
							var data = this.get('responseData');

							if(data.result == 'success'){
								addForm.hide();
								pendingMsg.show();
							}else if(data.result == 'failure'){
								if (data.message) {
									errorMsg.html(data.message);
								}

								errorMsg.show();
							}
						}
					}
				}
			);
		}
	);

	accountSelect.on(
		'change',
		function(event) {
			A.io.request('<portlet:resourceURL id="changeAccount"><portlet:param name="<%= Constants.CMD %>" value="changeAccount" /></portlet:resourceURL>',
				{
					dataType: 'json',
					form: {
						id: addForm.getDOM()
					},
					on: {
						success: function(event, id, obj) {
							var data = this.get('responseData');

							<portlet:namespace />refreshForm(data);
						}
					}
				}
			);
		}
	);

	lcsClusterEntrySelect.on(
		'change',
		function(event) {
	        if(lcsClusterEntrySelect.get('value') == 'undefined'){
				lcsClusterEntryNameInput.ancestor().ancestor().show();
			}else{
				lcsClusterEntryNameInput.ancestor().ancestor().hide();
			}
		}
	);

	Liferay.provide(
		window,
		'<portlet:namespace />refreshForm',
		function refreshForm(data){
			var corpEntries = data.corpEntries;

			if(corpEntries){
				A.Array.each(
					corpEntries,
					function(item, index, collection) {
						var option = A.Node.create('<option value="' + item.corpEntryId + '">' + item.name + '</option>');
						accountSelect.append(option);
					}
				);
			}

			var lcsClusterEntries = data.lcsClusterEntries;

			if(lcsClusterEntries){
				lcsClusterEntrySelect.html('');

				A.Array.each(
					lcsClusterEntries,
					function(item, index, collection) {
						var option = A.Node.create('<option value="' + item.lcsClusterEntryId + '">' + item.name + '</option>');
						lcsClusterEntrySelect.append(option);
					}
				);
			}
		}
	);
</aui:script>