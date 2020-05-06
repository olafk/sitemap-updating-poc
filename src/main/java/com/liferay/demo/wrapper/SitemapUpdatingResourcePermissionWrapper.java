/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.demo.wrapper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.ResourcePermissionServiceWrapper;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * DISCLAIMER
 * This is a quick <b>proof-of-concept</b> to manipulate the sitemap 
 * settings in dependency of guest-view permissions updates of 
 * public pages (layouts). 
 * 
 * This intercepts the operation triggered by 
 *   Site Builder / Pages / (individual page) / Permissions
 * and specificially the line with "Guest" permission in those.
 * 
 * For proper operation in typical use, more method overrides might
 * be necessary!
 * 
 * No warranty whatsoever for suitability for production use.
 *  
 * @author Olaf Kock 
 */

@Component(immediate = true, 
           service = ServiceWrapper.class)

public class SitemapUpdatingResourcePermissionWrapper extends ResourcePermissionServiceWrapper {

	public SitemapUpdatingResourcePermissionWrapper() {
		super(null);
	}
	
	public SitemapUpdatingResourcePermissionWrapper(ResourcePermissionService rps) {
		super(rps);
	}
	
	@Override
	public void setIndividualResourcePermissions(long groupId, long companyId, String name, String primKey,
			Map<Long, String[]> roleIdsToActionIds) throws PortalException {
		if(name.equals(Layout.class.getName())) {
			// note: no caching - probably not necessary: Layout Permissions updates are a rather rare operation
			// note: no error handling - assuming "guest" always returns a valid result.
			Role guestRole = roleLocalService.getRole(companyId, "guest");
			Layout layout = layoutLocalService.getLayout(Long.parseLong(primKey));
			// private layouts aren't included in sitemap anyway
			if(layout.isPublicLayout()) {
				String[] actions = roleIdsToActionIds.get(guestRole.getRoleId());
				UnicodeProperties props = layout.getTypeSettingsProperties();
				if(ArrayUtil.contains(actions, "VIEW", false)) {
					log.info("including page " + layout.getName() + " in sitemap due to guest VIEW permissions");
					props.setProperty("sitemap-include", "1");
				} else {
					log.info("excluding page " + layout.getName() + " in sitemap due to guest VIEW permissions");
					props.setProperty("sitemap-include", "0");
				}
				layout.setTypeSettingsProperties(props);
				layoutLocalService.updateLayout(layout);
			}
		}
		super.setIndividualResourcePermissions(groupId, companyId, name, primKey, roleIdsToActionIds);
	}
	
	@Reference 
	RoleLocalService roleLocalService;
	
	@Reference
	LayoutLocalService layoutLocalService;
	
	private Log log = LogFactoryUtil.getLog(SitemapUpdatingResourcePermissionWrapper.class);
}