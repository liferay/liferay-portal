/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {FDSTableCellHTMLElementBuilder} from '@liferay/js-api/data-set';

const badges = {
  "26590566": "Building Enterprise Websites with Liferay",
  "26590570": "Selling Liferay - Level 0",
  "27289153": "Mastering Liferay Workspaces and Tooling",
  "27433688": "Mastering Data Modeling with Liferay Objects",
  "28585350": "Foundations of Liferay Client Extensions",
  "28909886": "Mastering Frontend Liferay Client Extensions",
  "28957407": "Mastering Backend Liferay Client Extensions badge",
  "29437058": "Foundations of Liferay Headless API",
  "29768432": "Foundations of Modern Liferay Application Design",
  "30058888": "Mastering Liferay Design Elements",
  "30142498": "Mastering Consuming Liferay Headless APIs badge",
  "30142518": "Mastering Producing Liferay Headless APIs badge",
  "32885851": "Foundations of Content Management",
  "33522648": "Mastering Liferay Publishing Tools and the Content Life Cycle",
  "33669655": "Mastering Liferay Pages and Navigation",
  "34977044": "Mastering Search Engine Optimization with Liferay",
  "35427588": "Mastering Liferay Personalized Experiences",
  "35596066": "Mastering Liferay Content Search",
  "35958071": "Winning Sales Recipe",
  "36961102": "Foundations of Liferay Frontend Development",
  "37000447": "Mastering Liferay Assets and Content",
  "37128905": "Mastering Liferay Notifications",
  "37360441": "Mastering Commerce Users and Accounts",
  "37633198": "Foundations of Liferay Commerce",
  "38262953": "Mastering Liferay Product Management",
  "38496143": "Mastering Inventory Management with Liferay",
  "40113577": "Mastering Liferay Pricing and Promotions"
}


const fdsCellRenderer: FDSTableCellHTMLElementBuilder = ({itemData, value}) => {
	const element = document.createElement('div');
	const badgeTitle = badges[value as keyof typeof badges];

	element.innerHTML =
		badgeTitle || value.toString();

	return element;
};

export default fdsCellRenderer;
