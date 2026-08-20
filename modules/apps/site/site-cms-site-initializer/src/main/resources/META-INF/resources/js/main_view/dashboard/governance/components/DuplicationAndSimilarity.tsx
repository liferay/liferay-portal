/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import React, {useContext, useEffect, useState} from 'react';

import {openCMSModal} from '../../../../common/utils/openCMSModal';
import {SectionHeader} from '../../common/SectionHeader';
import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService from '../GovernanceService';
import {GovernanceAdditionalProps} from '../types';
import ReviewDuplicateTopicsModal from './ReviewDuplicateTopicsModal';

type DuplicationCard = {
	description: string;
	metric?: 'duplicateTopics';
	title: string;
};

const DUPLICATION_CARDS: DuplicationCard[] = [
	{
		description: Liferay.Language.get(
			'these-are-assets-covering-the-same-topic-based-on-similar-or-identical-titles'
		),
		metric: 'duplicateTopics',
		title: Liferay.Language.get('duplicate-topics'),
	},
	{
		description: Liferay.Language.get(
			'these-are-assets-with-significant-overlap-in-main-text-fields'
		),
		title: Liferay.Language.get('text-similarity'),
	},
	{
		description: Liferay.Language.get(
			'these-are-assets-with-identical-or-highly-similar-metadata'
		),
		title: Liferay.Language.get('same-metadata'),
	},
	{
		description: Liferay.Language.get(
			'these-are-assets-with-shared-or-frequently-reused-links-across-items'
		),
		title: Liferay.Language.get('similar-links'),
	},
];

const UNAVAILABLE_VALUE = '—';

export function DuplicationAndSimilarity({
	additionalProps,
	constants,
}: {
	additionalProps?: GovernanceAdditionalProps;
	constants: {[key: string]: string};
}) {
	const {space} = useContext(GovernanceContext);

	const [duplicateTopicsCount, setDuplicateTopicsCount] = useState<number>();
	const [entryClassNames, setEntryClassNames] = useState<string>();
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const controller = new AbortController();

		async function fetchEntryClassNames() {
			const entryClassNames =
				await GovernanceService.getCMSEntryClassNames(
					constants.ercContentStructures,
					constants.ercFileTypes,
					controller.signal
				);

			if (!controller.signal.aborted) {
				setEntryClassNames(entryClassNames);
			}
		}

		fetchEntryClassNames();

		return () => controller.abort();
	}, [constants.ercContentStructures, constants.ercFileTypes]);

	useEffect(() => {
		if (entryClassNames === undefined) {
			return;
		}

		const controller = new AbortController();

		async function fetchDuplicateTopicsCount(entryClassNames: string) {
			setLoading(true);

			const duplicateTopicsCount =
				await GovernanceService.getDuplicateTopicsCount({
					entryClassNames,
					signal: controller.signal,
					siteId: space.siteId,
				});

			if (!controller.signal.aborted) {
				setDuplicateTopicsCount(duplicateTopicsCount);
				setLoading(false);
			}
		}

		fetchDuplicateTopicsCount(entryClassNames);

		return () => controller.abort();
	}, [entryClassNames, space]);

	const title = Liferay.Language.get('duplication-and-similarity');

	const openReviewModal = () => {
		if (!entryClassNames) {
			return;
		}

		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<ReviewDuplicateTopicsModal
					additionalProps={additionalProps}
					closeModal={closeModal}
					entryClassNames={entryClassNames}
					siteId={space.siteId}
				/>
			),
			size: 'full-screen',
		});
	};

	return (
		<div aria-label={title} className="py-4" role="group">
			<SectionHeader icon="copy" title={title} />

			<ClayLayout.Row className="mt-3">
				{DUPLICATION_CARDS.map(({description, metric, title}) => {
					const value = metric ? duplicateTopicsCount : undefined;

					return (
						<ClayLayout.Col
							className="mb-3"
							key={title}
							md={6}
							xl={3}
						>
							<InteractiveCard
								aria-haspopup="dialog"
								description={description}
								loading={loading && Boolean(metric)}
								onClick={
									metric === 'duplicateTopics'
										? openReviewModal
										: undefined
								}
								title={title}
								value={
									isNullOrUndefined(value)
										? UNAVAILABLE_VALUE
										: value
								}
							/>
						</ClayLayout.Col>
					);
				})}
			</ClayLayout.Row>
		</div>
	);
}
