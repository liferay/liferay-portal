import acAccount from '../../../images/ac_account.svg';
import acApi from '../../../images/ac_api.svg';
import acAssets from '../../../images/ac_assets.svg';
import acBlock from '../../../images/ac_block.svg';
import acBlogs from '../../../images/ac_blogs.svg';
import acChart from '../../../images/ac_chart.svg';
import acCommerce from '../../../images/ac_commerce.svg';
import acDisplay from '../../../images/ac_display.svg';
import acDocumentsAndMedia from '../../../images/ac_documents_and_media.svg';
import acEventAnalysis from '../../../images/ac_event_analysis.svg';
import acEventIcon from '../../../images/ac_event_icon.svg';
import acGroup from '../../../images/ac_group.svg';
import acHidden from '../../../images/ac_hidden.svg';
import acHorizontalArrows from '../../../images/ac_horizontal_arrows.svg';
import acIndividual from '../../../images/ac_individual.svg';
import acIntegration from '../../../images/ac_integration.svg';
import acLifecycleEmpty from '../../../images/ac_lifecycle_empty.svg';
import acLogo from '../../../images/ac_logo.svg';
import acNoResultsFound from '../../../images/ac_no_results_found.svg';
import acNoSites from '../../../images/ac_no_sites.svg';
import acPage from '../../../images/ac_page.svg';
import acPageAnalytics from '../../../images/ac_page_analytics.svg';
import acQuestionMark from '../../../images/ac_question_mark.svg';
import acRule from '../../../images/ac_rule.svg';
import acSatellite from '../../../images/ac_satellite.svg';
import acSegment from '../../../images/ac_segment.svg';
import acStar from '../../../images/ac_star.svg';
import acTabletLandscape from '../../../images/ac_tablet_landscape.svg';
import acTest from '../../../images/ac_test.svg';
import acUsers from '../../../images/ac_users.svg';
import analyticsCloudLogo from '../../../images/analytics_cloud_logo.svg';
import analyticsOnboardingWelcome from '../../../images/analytics_onboarding_welcome.svg';
import cerebroThumbLineChart from '../../../images/cerebro_thumb_line_chart.svg';
import csvLogo from '../../../images/csv_logo.svg';
import dataPrivacyLock from '../../../images/data_privacy_lock.svg';
import definitions from '../../../images/definitions.svg';
import dxpCampaigns from '../../../images/dxp_campaigns.svg';
import dxpHome from '../../../images/dxp_home.svg';
import dxpIcon from '../../../images/dxp_icon.svg';
import dxpLogo from '../../../images/dxp_logo.svg';
import dxpLogoIso from '../../../images/dxp_logo_iso.svg';
import dxpSettings from '../../../images/dxp_settings.svg';
import dxpTouchpoints from '../../../images/dxp_touchpoints.svg';
import envelopeClose from '../../../images/envelope_close.svg';
import faroConnectionErrorOvals from '../../../images/faro_connection_error_ovals.svg';
import faroConnectionSuccessOvals from '../../../images/faro_connection_success_ovals.svg';
import faroContactsBarChart from '../../../images/faro_contacts_bar_chart.svg';
import faroContactsCardLayoutImageCenter from '../../../images/faro_contacts_card_layout_image_center.svg';
import faroContactsCardLayoutImageLeft from '../../../images/faro_contacts_card_layout_image_left.svg';
import faroContactsCardLayoutNoImage from '../../../images/faro_contacts_card_layout_no_image.svg';
import faroContactsCardTemplateTypeContact from '../../../images/faro_contacts_card_template_type_contact.svg';
import faroContactsCardTemplateTypeInfo from '../../../images/faro_contacts_card_template_type_info.svg';
import faroContactsCardTemplateTypeSegment from '../../../images/faro_contacts_card_template_type_segment.svg';
import faroContactsIndividuals from '../../../images/faro_contacts_individuals.svg';
import faroContactsPieChart from '../../../images/faro_contacts_pie_chart.svg';
import faroContactsSegments from '../../../images/faro_contacts_segments.svg';
import faroCsv from '../../../images/faro_csv.svg';
import faroDataSource from '../../../images/faro_data_source.svg';
import faroNoConnectionOvals from '../../../images/faro_no_connection_ovals.svg';
import faroPageViews from '../../../images/faro_page_views.svg';
import hubspot from '../../../images/hubspot.svg';
import increasingArrow from '../../../images/increasing_arrow.svg';
import individualDynamicSegment from '../../../images/individual_dynamic_segment.svg';
import individualStaticSegment from '../../../images/individual_static_segment.svg';
import ldpLogo from '../../../images/ldp_logo.svg';
import liferayDe from '../../../images/liferay_de.svg';
import liferayLogo from '../../../images/liferay_logo.svg';
import orderArrowAscending from '../../../images/order_arrow_ascending.svg';
import orderArrowDescending from '../../../images/order_arrow_descending.svg';
import ovals from '../../../images/ovals.svg';
import page from '../../../images/page.svg';
import React from 'react';
import salesforce from '../../../images/salesforce.svg';
import salesforceLogo from '../../../images/salesforce_logo.svg';
import sessionMultipleChart from '../../../images/session_multiple_chart.svg';
import sessionSingleChart from '../../../images/session_single_chart.svg';
import usage from '../../../images/usage.svg';
import userManagement from '../../../images/user_management.svg';

/**
 * The Analytics Cloud icons, keyed by the symbol name the app renders them by.
 * Each SVG is bundled as a React component by SVGR, so `Icon` draws these
 * inline instead of looking them up in the Clay sprite.
 */

const AC_ICONS: Record<
	string,
	React.ForwardRefExoticComponent<
		React.SVGProps<SVGSVGElement> & React.RefAttributes<SVGSVGElement>
	>
> = {
	ac_account: acAccount,
	ac_api: acApi,
	ac_assets: acAssets,
	ac_block: acBlock,
	ac_blogs: acBlogs,
	ac_chart: acChart,
	ac_commerce: acCommerce,
	ac_display: acDisplay,
	ac_documents_and_media: acDocumentsAndMedia,
	ac_event_analysis: acEventAnalysis,
	ac_event_icon: acEventIcon,
	ac_group: acGroup,
	ac_hidden: acHidden,
	ac_horizontal_arrows: acHorizontalArrows,
	ac_individual: acIndividual,
	ac_integration: acIntegration,
	ac_lifecycle_empty: acLifecycleEmpty,
	ac_logo: acLogo,
	ac_no_results_found: acNoResultsFound,
	ac_no_sites: acNoSites,
	ac_page: acPage,
	ac_page_analytics: acPageAnalytics,
	ac_question_mark: acQuestionMark,
	ac_rule: acRule,
	ac_satellite: acSatellite,
	ac_segment: acSegment,
	ac_star: acStar,
	ac_tablet_landscape: acTabletLandscape,
	ac_test: acTest,
	ac_users: acUsers,
	analytics_cloud_logo: analyticsCloudLogo,
	analytics_onboarding_welcome: analyticsOnboardingWelcome,
	cerebro_thumb_line_chart: cerebroThumbLineChart,
	csv_logo: csvLogo,
	data_privacy_lock: dataPrivacyLock,
	definitions,
	dxp_campaigns: dxpCampaigns,
	dxp_home: dxpHome,
	dxp_icon: dxpIcon,
	dxp_logo: dxpLogo,
	dxp_logo_iso: dxpLogoIso,
	dxp_settings: dxpSettings,
	dxp_touchpoints: dxpTouchpoints,
	envelope_close: envelopeClose,
	faro_connection_error_ovals: faroConnectionErrorOvals,
	faro_connection_success_ovals: faroConnectionSuccessOvals,
	faro_contacts_bar_chart: faroContactsBarChart,
	faro_contacts_card_layout_image_center: faroContactsCardLayoutImageCenter,
	faro_contacts_card_layout_image_left: faroContactsCardLayoutImageLeft,
	faro_contacts_card_layout_no_image: faroContactsCardLayoutNoImage,
	faro_contacts_card_template_type_contact:
		faroContactsCardTemplateTypeContact,
	faro_contacts_card_template_type_info: faroContactsCardTemplateTypeInfo,
	faro_contacts_card_template_type_segment:
		faroContactsCardTemplateTypeSegment,
	faro_contacts_individuals: faroContactsIndividuals,
	faro_contacts_pie_chart: faroContactsPieChart,
	faro_contacts_segments: faroContactsSegments,
	faro_csv: faroCsv,
	faro_data_source: faroDataSource,
	faro_no_connection_ovals: faroNoConnectionOvals,
	faro_page_views: faroPageViews,
	hubspot,
	increasing_arrow: increasingArrow,
	individual_dynamic_segment: individualDynamicSegment,
	individual_static_segment: individualStaticSegment,
	ldp_logo: ldpLogo,
	liferay_de: liferayDe,
	liferay_logo: liferayLogo,
	order_arrow_ascending: orderArrowAscending,
	order_arrow_descending: orderArrowDescending,
	ovals,
	page,
	salesforce,
	salesforce_logo: salesforceLogo,
	session_multiple_chart: sessionMultipleChart,
	session_single_chart: sessionSingleChart,
	usage,
	user_management: userManagement,
};

export default AC_ICONS;
