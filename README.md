# Proof of Concept: Sitemap Updater

## Problem description

When the Guest VIEW permission is removed from one of Liferay's public 
pages, the page stays included in the sitemap, as the sitemap setting
is independent of the permissions. The resulting access-related error 
message is likely to be punished by search engines. Thus, a change to 
permissions should automatically remove a page from, or include it in
the sitemap.

## Build instructions

This project was built within a Liferay Workspace's `modules` directory
in Liferay DXP 7.2 FP4, configured with target platform 7.2.10.1

# DISCLAIMER


This is a quick **proof-of-concept** (POC) to manipulate the sitemap 
settings in dependency of guest-view permissions updates of 
public pages (layouts). 

This POC intercepts/implements operations triggered by 

    Site Builder / Pages / (individual page) / Permissions

and specificially the line with "Guest" permission in those.

For proper operation in typical use, more method overrides might
be necessary!

**No warranty whatsoever for suitability for production use!**

