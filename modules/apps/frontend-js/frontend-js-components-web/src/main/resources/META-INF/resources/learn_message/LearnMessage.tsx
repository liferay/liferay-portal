/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

export const LearnResourcesContext = React.createContext<
	Partial<ILearnResourceContext>
>({});

interface ILearnResourceLocaleItem {
	message: string;
	url?: string;
}

interface ILearnResourceKeyItem {
	[locale: string]: ILearnResourceLocaleItem;
}

interface ILearnResourceItem {
	[resourceKey: string]: ILearnResourceKeyItem;
}

export interface ILearnResourceContext {
	[learnResourceName: string]: ILearnResourceItem;
}

type ClayLinkProps = React.ComponentProps<typeof ClayLink>;

interface IProps extends ClayLinkProps {
	className?: string;

	/**
	 * The learn resource
	 */
	resource: string;

	/**
	 * The key to render.
	 */
	resourceKey: string;
}

/**
 * This component is used to render links to Liferay Learn articles. The json
 * object `learnResources` contains the messages and urls and is taken from
 * liferay-portal/learn-resources.
 *
 * Use `LearnResourcesContext` to wrap the entire React App and in the JSP use
 * `LearnMessageUtil.getReactDataJSONObject` to get the required resources.
 *
 * Example use:
 * <LearnResourcesContext.Provider value={learnResources}>
 * 	<LearnMessage resourceKey="general" resource="portlet-configuration-web" />
 * </LearnResourcesContext.Provider>
 *
 * Example of `learnResources`:
 * {
 * 	"portlet-configuration-web": { // Learn resource
 *		"general": { // Resource key
 *			"en_US": {
 *				"message": "Tell me more",
 *				"url": "https://learn.liferay.com/"
 *			}
 *		}
 * 	}
 * }
 */
export default function LearnMessage({
	className = '',
	resource,
	resourceKey,
	...otherProps
}: IProps) {
	const learnResourcesContext = useContext(
		LearnResourcesContext
	) as ILearnResourceContext;

	if (
		process.env.NODE_ENV === 'development' &&
		!Object.keys(learnResourcesContext).length
	) {
		console.warn(
			`Unable to render LearnMessage for resourceKey: '${resourceKey}'.`,
			'Unable to find LearnResourcesContext.',
			'To LearnMessage component must be wrapped with',
			'<LearnResourcesContext.Provider value={learnResources}>',
			'with the needed learnResources passed in using the Java method',
			'LearnMessageUtil.getReactDataJSONObject("module-name").'
		);
	}

	const resourceKeyObject = learnResourcesContext?.[resource]?.[
		resourceKey
	] || {
		en_US: {},
	};

	if (
		process.env.NODE_ENV === 'development' &&
		!learnResourcesContext?.[resource]
	) {
		console.warn(
			`Unable to render LearnMessage for resourceKey: '${resourceKey}'.`,
			`Unable to find resource: '${resource}'`
		);
	}

	const learnMessageObject =
		resourceKeyObject[Liferay.ThemeDisplay.getLanguageId()] ||
		resourceKeyObject[Liferay.ThemeDisplay.getDefaultLanguageId()] ||
		resourceKeyObject[Object.keys(resourceKeyObject)[0]];

	if (learnMessageObject.url) {
		return (
			<ClayLink
				aria-label={sub(
					Liferay.Language.get('x-opens-new-window'),
					learnMessageObject.message
				)}
				className={getCN('learn-message', className)}
				href={learnMessageObject.url}
				rel="noopener noreferrer"
				target="_blank"
				{...otherProps}
			>
				{learnMessageObject.message}

				<ClayIcon className="ml-1" fontSize={14} symbol="shortcut" />
			</ClayLink>
		);
	}

	return <></>;
}
