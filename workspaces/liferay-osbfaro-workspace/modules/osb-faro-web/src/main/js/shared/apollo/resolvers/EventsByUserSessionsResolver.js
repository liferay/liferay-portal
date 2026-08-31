/**
 * The campaign identity a session's touch resolved to. `utmCampaignId` is the
 * raw value extracted from the tenant's configured campaign-identity query
 * param, and is present whenever a touch carried one. `utmCampaignName` is the
 * Salesforce Campaign it joined against, and stays null when the id matched no
 * stored campaign — the two together distinguish an unresolved touch from a
 * page that carried no campaign at all.
 */
const RESOLVED_CAMPAIGN = {
	utmCampaignId: '7013a000002QwErtAAG',
	utmCampaignName: 'Spring Compactor Promo 2026',
};

const UNRESOLVED_CAMPAIGN = {
	utmCampaignId: '7013a000002XyZbAAK',
	utmCampaignName: null,
};

const NO_CAMPAIGN = {
	utmCampaignId: null,
	utmCampaignName: null,
};

export default () => ({
	__typename: 'EventsByUserSession',
	totalEvents: 17,
	totalPageGroupsMetric: {__typename: 'Metric', value: 12},
	userSessions: [

		// An in-progress DXP session whose touches carry a campaign identity:
		// two page groups resolve to a Salesforce Campaign, one resolves to
		// nothing (id only), and one carried no campaign at all.

		{
			__typename: 'UserSession',
			becameKnown: false,
			browserName: 'Chrome',
			completeDate: null,
			contentLanguageId: 'en-US',
			createDate: 'Mon Aug 31 20:00:04 GMT 2026',
			devicePixelRatio: '2',
			deviceType: 'Desktop',
			events: [
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle:
						'Tandem Rollers & Compactors - Full Product Range | RoadTech',
					canonicalUrl:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					createDate: 'Mon Aug 31 20:20:15 GMT 2026',
					eventDate: '2026-08-31T20:20:15.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					pageKeywords: '',
					pageTitle:
						'Tandem Rollers & Compactors - Full Product Range | RoadTech',
					properties: [
						{name: 'utm_source', value: 'salesforce'},
						{name: 'utm_cid', value: '7013a000002QwErtAAG'},
					],
					referrer:
						'https://marketplace.roadtech.com/lp/spring-compactor-promo',
					url: 'https://marketplace.roadtech.com/compactors/tandem-rollers?utm_cid=7013a000002QwErtAAG',
					...RESOLVED_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'CustomEvent',
					assetTitle: 'Compare Models',
					canonicalUrl:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					createDate: 'Mon Aug 31 20:20:02 GMT 2026',
					eventDate: '2026-08-31T20:20:02.000Z',
					eventId: 'elementClicked',
					name: 'elementClicked',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					pageKeywords: '',
					pageTitle:
						'Tandem Rollers & Compactors - Full Product Range | RoadTech',
					properties: [{name: 'elementId', value: 'compare-models'}],
					referrer:
						'https://marketplace.roadtech.com/lp/spring-compactor-promo',
					url: 'https://marketplace.roadtech.com/compactors/tandem-rollers',
					...RESOLVED_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle:
						'Tandem Rollers & Compactors - Full Product Range | RoadTech',
					canonicalUrl:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					createDate: 'Mon Aug 31 20:19:48 GMT 2026',
					eventDate: '2026-08-31T20:19:48.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					pageKeywords: '',
					pageTitle:
						'Tandem Rollers & Compactors - Full Product Range | RoadTech',
					properties: [
						{name: 'utm_source', value: 'salesforce'},
						{name: 'utm_cid', value: '7013a000002QwErtAAG'},
					],
					referrer:
						'https://marketplace.roadtech.com/videos/hamm-hd90i-demo',
					url: 'https://marketplace.roadtech.com/compactors/tandem-rollers?utm_cid=7013a000002QwErtAAG',
					...RESOLVED_CAMPAIGN,
				},

				// A page reached from inside the site, with no campaign
				// identity on it — the row that must stay label free.

				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Request a Quote - Hamm HD+ 90i Tandem Roller',
					canonicalUrl:
						'https://marketplace.roadtech.com/compactors/hamm-hd90i/quote',
					createDate: 'Mon Aug 31 20:18:42 GMT 2026',
					eventDate: '2026-08-31T20:18:42.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/compactors/hamm-hd90i/quote',
					pageKeywords: '',
					pageTitle: 'Request a Quote - Hamm HD+ 90i Tandem Roller',
					properties: [],
					referrer:
						'https://marketplace.roadtech.com/compactors/tandem-rollers',
					url: 'https://marketplace.roadtech.com/compactors/hamm-hd90i/quote',
					...NO_CAMPAIGN,
				},

				// A touch whose campaign identity matched no stored Salesforce
				// Campaign: the raw id is kept, the name stays null.

				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle:
						'Hamm HD+ 901 Live Compaction Demo - Asphalt Paving',
					canonicalUrl:
						'https://marketplace.roadtech.com/videos/hamm-hd90i-demo',
					createDate: 'Mon Aug 31 20:15:30 GMT 2026',
					eventDate: '2026-08-31T20:15:30.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/videos/hamm-hd90i-demo',
					pageKeywords: '',
					pageTitle:
						'Hamm HD+ 901 Live Compaction Demo - Asphalt Paving',
					properties: [
						{name: 'utm_source', value: 'linkedin'},
						{name: 'utm_cid', value: '7013a000002XyZbAAK'},
					],
					referrer: 'https://www.linkedin.com/',
					url: 'https://marketplace.roadtech.com/videos/hamm-hd90i-demo?utm_cid=7013a000002XyZbAAK',
					...UNRESOLVED_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'CustomEvent',
					assetTitle: 'Play Demo Video',
					canonicalUrl:
						'https://marketplace.roadtech.com/videos/hamm-hd90i-demo',
					createDate: 'Mon Aug 31 20:15:22 GMT 2026',
					eventDate: '2026-08-31T20:15:22.000Z',
					eventId: 'videoPlayed',
					name: 'videoPlayed',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/videos/hamm-hd90i-demo',
					pageKeywords: '',
					pageTitle:
						'Hamm HD+ 901 Live Compaction Demo - Asphalt Paving',
					properties: [{name: 'videoId', value: 'hamm-hd90i-demo'}],
					referrer: 'https://www.linkedin.com/',
					url: 'https://marketplace.roadtech.com/videos/hamm-hd90i-demo',
					...UNRESOLVED_CAMPAIGN,
				},

				// The landing page the campaign link pointed at, the entry
				// touch of the session.

				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Spring Compactor Promo - Landing | RoadTech',
					canonicalUrl:
						'https://marketplace.roadtech.com/lp/spring-compactor-promo',
					createDate: 'Mon Aug 31 20:05:10 GMT 2026',
					eventDate: '2026-08-31T20:05:10.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://marketplace.roadtech.com/lp/spring-compactor-promo',
					pageKeywords: '',
					pageTitle: 'Spring Compactor Promo - Landing | RoadTech',
					properties: [
						{name: 'utm_source', value: 'salesforce'},
						{name: 'utm_cid', value: '7013a000002QwErtAAG'},
					],
					referrer: 'https://mail.google.com/',
					url: 'https://marketplace.roadtech.com/lp/spring-compactor-promo?utm_cid=7013a000002QwErtAAG',
					...RESOLVED_CAMPAIGN,
				},
			],
			individualId: 'e5d1f0a8-3c47-4b92-9a11-77c0b2d4e6f3',
			languageId: 'en-US',
			screenHeight: '1440',
			screenWidth: '2560',
			timezoneOffset: '-03:00',
			userAgent:
				'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36',
			userId: '32901',
			userName: 'Michelle de Rue',
		},

		// A completed session for the same individual on the previous day,
		// with no campaign identity anywhere — the baseline the campaign
		// session is read against.

		{
			__typename: 'UserSession',
			becameKnown: false,
			browserName: 'Chrome Mobile',
			completeDate: 'Sun Aug 30 14:41:19 GMT 2026',
			contentLanguageId: 'en-US',
			createDate: 'Sun Aug 30 14:38:02 GMT 2026',
			devicePixelRatio: '3',
			deviceType: 'Smartphone',
			events: [
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Support - RoadTech',
					canonicalUrl: 'https://marketplace.roadtech.com/support',
					createDate: 'Sun Aug 30 14:41:19 GMT 2026',
					eventDate: '2026-08-30T14:41:19.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://marketplace.roadtech.com/support',
					pageKeywords: '',
					pageTitle: 'Support - RoadTech',
					properties: [],
					referrer: 'https://marketplace.roadtech.com/',
					url: 'https://marketplace.roadtech.com/support',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Home - RoadTech',
					canonicalUrl: 'https://marketplace.roadtech.com',
					createDate: 'Sun Aug 30 14:38:02 GMT 2026',
					eventDate: '2026-08-30T14:38:02.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://marketplace.roadtech.com',
					pageKeywords: '',
					pageTitle: 'Home - RoadTech',
					properties: [],
					referrer: '',
					url: 'https://marketplace.roadtech.com/',
					...NO_CAMPAIGN,
				},
			],
			individualId: 'e5d1f0a8-3c47-4b92-9a11-77c0b2d4e6f3',
			languageId: 'en-US',
			screenHeight: '844',
			screenWidth: '390',
			timezoneOffset: '-03:00',
			userAgent:
				'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/121.0.0.0 Mobile/15E148 Safari/604.1',
			userId: '32901',
			userName: 'Michelle de Rue',
		},
		{
			__typename: 'UserSession',
			becameKnown: false,
			browserName: 'Default Browser',
			completeDate: 'Fri May 08 18:00:15 GMT 2026',
			contentLanguageId: null,
			createDate: 'Fri May 08 17:59:59 GMT 2026',
			devicePixelRatio: '',
			deviceType: 'Unknown',
			events: [
				{
					__typename: 'Event',
					applicationId: 'HubSpot',
					assetTitle: null,
					canonicalUrl: 'https://hubspot.com',
					createDate: 'Fri May 08 18:00:15 GMT 2026',
					eventDate: '2026-05-08T18:00:15.000Z',
					eventId: 'emailView',
					name: 'emailView',
					pageDescription: null,
					pageGroupId: null,
					pageKeywords: null,
					pageTitle: null,
					properties: [
						{name: 'email', value: 'john.doe@example.com'},
						{name: 'subject', value: 'Welcome Newsletter'},
					],
					referrer: 'https://hubspot.com',
					url: 'https://hubspot.com',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'HubSpot',
					assetTitle: null,
					canonicalUrl: 'https://hubspot.com',
					createDate: 'Fri May 08 17:59:59 GMT 2026',
					eventDate: '2026-05-08T17:59:59.000Z',
					eventId: 'formSubmit',
					name: 'formSubmit',
					pageDescription: null,
					pageGroupId: null,
					pageKeywords: null,
					pageTitle: null,
					properties: [
						{name: 'formId', value: 'abc123'},
						{
							name: 'pageUrl',
							value: 'https://hubspot.com/landing-page',
						},
					],
					referrer: 'https://hubspot.com',
					url: 'https://hubspot.com',
					...NO_CAMPAIGN,
				},
			],
			individualId: null,
			languageId: null,
			screenHeight: '',
			screenWidth: '',
			timezoneOffset: null,
			userAgent: 'HubSpot Webhook',
			userId: null,
			userName: null,
		},
		{
			__typename: 'UserSession',
			becameKnown: true,
			browserName: 'Chrome Mobile',
			completeDate: 'Thu May 07 20:15:10 GMT 2026',
			contentLanguageId: 'en-US',
			createDate: 'Thu May 07 20:10:05 GMT 2026',
			devicePixelRatio: '3',
			deviceType: 'Smartphone',
			events: [
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Home - learn-dev.lxc.liferay.com',
					canonicalUrl: 'https://learn-dev.liferay.com/home',
					createDate: 'Thu May 07 20:15:05 GMT 2026',
					eventDate: '2026-05-07T20:15:05.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://learn-dev.liferay.com/home',
					pageKeywords: '',
					pageTitle: 'Home - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer: '',
					url: 'https://learn-dev.liferay.com/home',
					...NO_CAMPAIGN,
				},
			],
			individualId: null,
			languageId: 'en-US',
			screenHeight: '844',
			screenWidth: '390',
			timezoneOffset: '-03:00',
			userAgent:
				'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/121.0.0.0 Mobile/15E148 Safari/604.1',
			userId: null,
			userName: null,
		},
		{
			__typename: 'UserSession',
			becameKnown: false,
			browserName: 'Chrome',
			completeDate: 'Thu May 07 19:57:32 GMT 2026',
			contentLanguageId: 'en-US',
			createDate: 'Thu May 07 19:37:32 GMT 2026',
			devicePixelRatio: '1.5',
			deviceType: 'Desktop',
			events: [
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle:
						'Content Management System - learn-dev.lxc.liferay.com',
					canonicalUrl:
						'https://learn-dev.liferay.com/capabilities/content-management-system',
					createDate: 'Thu May 07 19:57:21 GMT 2026',
					eventDate: '2026-05-07T19:57:21.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://learn-dev.liferay.com/capabilities/content-management-system',
					pageKeywords: '',
					pageTitle:
						'Content Management System - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer: 'https://learn-dev.liferay.com/home',
					url: 'https://learn-dev.liferay.com/capabilities/content-management-system',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Home - learn-dev.lxc.liferay.com',
					canonicalUrl: 'https://learn-dev.liferay.com/home',
					createDate: 'Thu May 07 19:56:55 GMT 2026',
					eventDate: '2026-05-07T19:56:55.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://learn-dev.liferay.com/home',
					pageKeywords: '',
					pageTitle: 'Home - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer: 'https://learn-dev.liferay.com/c/portal/logout',
					url: 'https://learn-dev.liferay.com/home',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Home - learn-dev.lxc.liferay.com',
					canonicalUrl: 'https://learn-dev.liferay.com',
					createDate: 'Thu May 07 19:56:21 GMT 2026',
					eventDate: '2026-05-07T19:56:21.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://learn-dev.liferay.com',
					pageKeywords: '',
					pageTitle: 'Home - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer: '',
					url: 'https://learn-dev.liferay.com/',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Home - learn-dev.lxc.liferay.com',
					canonicalUrl: 'https://learn-dev.liferay.com',
					createDate: 'Thu May 07 19:38:43 GMT 2026',
					eventDate: '2026-05-07T19:38:43.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://learn-dev.liferay.com',
					pageKeywords: '',
					pageTitle: 'Home - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer:
						'https://learn-dev.liferay.com/capabilities/security',
					url: 'https://learn-dev.liferay.com/',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Security - learn-dev.lxc.liferay.com',
					canonicalUrl:
						'https://learn-dev.liferay.com/capabilities/security',
					createDate: 'Thu May 07 19:38:36 GMT 2026',
					eventDate: '2026-05-07T19:38:36.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId:
						'https://learn-dev.liferay.com/capabilities/security',
					pageKeywords: '',
					pageTitle: 'Security - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer: 'https://learn-dev.liferay.com/',
					url: 'https://learn-dev.liferay.com/capabilities/security',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'Page',
					assetTitle: 'Home - learn-dev.lxc.liferay.com',
					canonicalUrl: 'https://learn-dev.liferay.com',
					createDate: 'Thu May 07 19:37:43 GMT 2026',
					eventDate: '2026-05-07T19:37:43.000Z',
					eventId: 'pageViewed',
					name: 'pageViewed',
					pageDescription: '',
					pageGroupId: 'https://learn-dev.liferay.com',
					pageKeywords: '',
					pageTitle: 'Home - learn-dev.lxc.liferay.com',
					properties: [
						{
							name: 'externalReferenceCode',
							value: '2d420977-ed76-97d2-4478-379f03130595',
						},
					],
					referrer:
						'https://learn-dev.liferay.com/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fview_configuration_screen&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_configurationScreenKey=analytics-cloud-connection',
					url: 'https://learn-dev.liferay.com/',
					...NO_CAMPAIGN,
				},
			],
			individualId: null,
			languageId: 'en-US',
			screenHeight: '1321',
			screenWidth: '2560',
			timezoneOffset: '-03:00',
			userAgent:
				'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36',
			userId: null,
			userName: null,
		},
		{
			__typename: 'UserSession',
			becameKnown: false,
			browserName: 'Default Browser',
			completeDate: 'Thu May 07 21:15:44 GMT 2026',
			contentLanguageId: null,
			createDate: 'Thu May 07 21:15:44 GMT 2026',
			devicePixelRatio: '',
			deviceType: 'Unknown',
			events: [
				{
					__typename: 'Event',
					applicationId: 'HubSpot',
					assetTitle: null,
					canonicalUrl: 'https://hubspot.com',
					createDate: 'Thu May 07 21:15:44 GMT 2026',
					eventDate: '2026-05-07T21:15:44.000Z',
					eventId: 'emailOpen',
					name: 'emailOpen',
					pageDescription: null,
					pageGroupId: null,
					pageKeywords: null,
					pageTitle: null,
					properties: [
						{name: 'email', value: 'john.doe@example.com'},
						{name: 'subject', value: 'May Product Updates'},
					],
					referrer: 'https://hubspot.com',
					url: 'https://hubspot.com',
					...NO_CAMPAIGN,
				},
				{
					__typename: 'Event',
					applicationId: 'HubSpot',
					assetTitle: null,
					canonicalUrl: 'https://hubspot.com',
					createDate: 'Thu May 07 21:15:44 GMT 2026',
					eventDate: '2026-05-07T21:15:44.000Z',
					eventId: 'linkClick',
					name: 'linkClick',
					pageDescription: null,
					pageGroupId: null,
					pageKeywords: null,
					pageTitle: null,
					properties: [
						{
							name: 'linkUrl',
							value: 'https://liferay.com/products',
						},
						{name: 'email', value: 'john.doe@example.com'},
					],
					referrer: 'https://hubspot.com',
					url: 'https://hubspot.com',
					...NO_CAMPAIGN,
				},
			],
			individualId: null,
			languageId: null,
			screenHeight: '',
			screenWidth: '',
			timezoneOffset: null,
			userAgent: 'HubSpot Webhook',
			userId: null,
			userName: null,
		},
	],
});
