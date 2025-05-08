/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ReactNode} from 'react';

import PicklistService from '../../../../src/main/resources/META-INF/resources/js/services/PicklistService';
import SpaceService from '../../../../src/main/resources/META-INF/resources/js/services/SpaceService';
import {CacheContext} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/CacheContext';
import {Picklist} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Picklist';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Space';

export const broadcastRefMock = {
	current: {
		addEventListener: jest.fn(),
		postMessage: jest.fn(),
		removeEventListener: jest.fn(),
	} as unknown as BroadcastChannel,
};

function getCache({
	picklists,
	spaces,
}: {
	picklists?: Picklist[];
	spaces?: Space[];
}) {
	return {
		picklists: {
			data: picklists || [],
			fetcher: PicklistService.getPicklists,
			status: picklists ? ('saved' as const) : ('idle' as const),
		},
		spaces: {
			data: spaces || [],
			fetcher: SpaceService.getSpaces,
			status: spaces ? ('saved' as const) : ('idle' as const),
		},
	};
}

export function MockCacheProvider({
	children,
	picklists,
	spaces,
}: {
	children: ReactNode;
	picklists?: Picklist[];
	spaces?: Space[];
}) {
	return (
		<CacheContext.Provider
			value={{
				broadcastRef: broadcastRefMock,
				cache: getCache({picklists, spaces}),
				update: () => {},
			}}
		>
			{children}
		</CacheContext.Provider>
	);
}
