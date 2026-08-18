/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '@liferay/frontend-js-state-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {useEffect} from 'react';

import {useSelector} from '../contexts/StoreContext';
import selectSegmentsExperienceId from '../selectors/selectSegmentsExperienceId';
import InfoItemService from '../services/InfoItemService';

export type PageContent = {
	actions: {
		editImage?: {
			editImageURL: string;
			fileEntryId: string;
			previewURL: string;
		};
		editURL?: string;
		permissionsURL?: string;
		viewUsagesURL?: string;
	};
	className: string;
	classNameId: string;
	classPK: string;
	classTypeId: number;
	externalReferenceCode: string;
	icon: string;
	isRestricted: boolean;
	status: {
		hasApprovedVersion: boolean;
		label: string;
		style: string;
	};
	subtype: string;
	title: string;
	type: string;
	usagesCount: number;
};

type ContentsState = {
	data?: PageContent[];
	status: 'idle' | 'loading' | 'saved';
};

const INITIAL_STATE: ContentsState = {
	data: [],
	status: 'idle',
};

export const pageContentsAtom = State.atom('page-contents', INITIAL_STATE);

const EMPTY_ARRAY: PageContent[] = [];

export default function usePageContents() {
	const segmentsExperienceId = useSelector(selectSegmentsExperienceId);

	const [contents, setContents] = useLiferayState(pageContentsAtom);

	useEffect(() => {
		Liferay.once('endNavigate', clearPageContents);
	}, []);

	useEffect(() => {
		const {status} = State.readAtom(pageContentsAtom);

		if (status === 'idle') {
			setContents({status: 'loading'});

			InfoItemService.getPageContents({
				segmentsExperienceId,
			}).then((pageContents) => {
				setContents({data: pageContents, status: 'saved'});
			});
		}
	}, [contents, segmentsExperienceId, setContents]);

	return contents.data || EMPTY_ARRAY;
}

export function clearPageContents() {
	State.writeAtom(pageContentsAtom, INITIAL_STATE);
}
