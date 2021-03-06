<#--
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
-->

<#include "../init.ftl" />

<#assign campaignsLabel = languageUtil.get(locale, "campaigns") />

<#if campaigns?has_content>
	<#assign campaignsLabel = campaignsLabel + " <span class=\"badge badge-info\">" + campaigns?size + "</span>" />
</#if>

<#assign userSegmentsLabel = languageUtil.get(locale, "user-segments") />

<#if userSegments?has_content>
	<#assign userSegmentsLabel = userSegmentsLabel + " <span class=\"badge badge-info\">" + userSegments?size + "</span>" />
</#if>

<@liferay_ui["tabs"]
	names="${userSegmentsLabel},${campaignsLabel}"
	refresh=false
	type="pills"
	value="${tabs1}"
>
	<@liferay_ui["section"]>
		<#include "view_user_segments.ftl" />
	</@>

	<@liferay_ui["section"]>
		<#include "view_campaigns.ftl" />
	</@>
</@>