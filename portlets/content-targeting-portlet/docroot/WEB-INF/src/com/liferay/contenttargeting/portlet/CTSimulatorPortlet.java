/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.contenttargeting.portlet;

import com.liferay.contenttargeting.api.model.UserSegmentSimulator;
import com.liferay.contenttargeting.model.UserSegment;
import com.liferay.contenttargeting.service.UserSegmentLocalService;
import com.liferay.contenttargeting.util.ContentTargetingUtil;
import com.liferay.contenttargeting.util.WebKeys;
import com.liferay.osgi.util.ServiceTrackerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import freemarker.ext.beans.BeansWrapper;

import freemarker.template.TemplateHashModel;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Julio Camarero
 */
public class CTSimulatorPortlet extends CTFreeMarkerPortlet {

	@Override
	public void init() throws PortletException {
		super.init();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		if (bundle == null) {
			throw new UnavailableException(
				"Can't find a reference to the OSGi bundle") {

				@Override
				public boolean isPermanent() {
					return true;
				}
			};
		}

		_userSegmentLocalService = ServiceTrackerUtil.getService(
			UserSegmentLocalService.class, bundle.getBundleContext());

		_userSegmentSimulator = ServiceTrackerUtil.getService(
			UserSegmentSimulator.class, bundle.getBundleContext());
	}

	public void stopSimulation(ActionRequest request, ActionResponse response)
		throws Exception {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(request);

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(response);

		_userSegmentSimulator.removeAllUserSegmentIds(
			httpServletRequest, httpServletResponse);
	}

	public void simulateUserSegment(ActionRequest request, ActionResponse response)
		throws Exception {

		long userSegmentId = ParamUtil.getLong(request, "userSegmentId");

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(request);

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(response);

		_userSegmentSimulator.addUserSegmentId(
			userSegmentId, httpServletRequest, httpServletResponse);
	}

	protected void populateContext(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, Template template)
		throws Exception {

		BeansWrapper wrapper = BeansWrapper.getDefaultInstance();

		TemplateHashModel staticModels = wrapper.getStaticModels();

		template.put("currentURL", PortalUtil.getCurrentURL(portletRequest));
		template.put(
			"redirect", ParamUtil.getString(portletRequest, "redirect"));

		populateViewContext(
			path, portletRequest, portletResponse, template, staticModels);
	}

	protected void populateViewContext(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, Template template,
			TemplateHashModel staticModels)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] userSegmentIds = (long[])portletRequest.getAttribute(
			WebKeys.USER_SEGMENT_IDS);

		template.put("userSegmentIds", userSegmentIds);

		List<UserSegment> userSegments = new ArrayList<UserSegment>();

		if (userSegmentIds != null) {
			for (long userSegmentId : userSegmentIds) {
				userSegments.add(
					_userSegmentLocalService.getUserSegment(userSegmentId));
			}
		}

		template.put("userSegments", userSegments);

		long[] groupIds = ContentTargetingUtil.getAncestorsAndCurrentGroupIds(
			themeDisplay.getSiteGroupId());

		template.put(
			"availableUserSegments",
			_userSegmentLocalService.getUserSegments(groupIds));
	}

	private static Log _log = LogFactoryUtil.getLog(CTSimulatorPortlet.class);

	private UserSegmentLocalService _userSegmentLocalService;

	private UserSegmentSimulator _userSegmentSimulator;

}