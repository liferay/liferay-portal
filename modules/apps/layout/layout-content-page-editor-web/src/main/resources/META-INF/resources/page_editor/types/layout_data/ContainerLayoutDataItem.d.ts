/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ContentDisplayOption} from '../../app/config/constants/contentDisplayOptions';
import {VerticalAlignmentOption} from '../../plugins/browser/components/page_structure/components/item_configuration_panels/collection_general_panel/VerticalAlignmentSelector';
import {
	BaseLayoutDataItem,
	CommonStyles,
	ResponsiveConfig,
	TranslatedConfig,
} from './BaseLayoutDataItem';

type HtmlTag =
	| 'article'
	| 'aside'
	| 'div'
	| 'footer'
	| 'header'
	| 'nav'
	| 'section';

type LinkTarget = '_blank' | '_parent' | '_self' | '_top';

export type ContainerGeneralConfig = ResponsiveConfig<{
	align?: VerticalAlignmentOption;
	contentDisplay?: ContentDisplayOption;
	flexWrap?: 'flex-wrap';
	justify?: 'justify-content-end';
	widthType?: 'fixed';
}>;

export type ContainerLayoutDataItem = BaseLayoutDataItem<
	'container',
	CommonStyles &
		ContainerGeneralConfig & {
			contentVisibility?: string;
			htmlTag?: HtmlTag | '';
			indexed?: boolean;
			link?: {
				href: TranslatedConfig<string>;
				target?: LinkTarget | '';
			};
		}
>;
