/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../../FrontendDataSetContext';

import type {
	FDSVisualizationMode,
	FDSVisualizationModeArgs,
	FDSVisualizationModeInstance,
	FDSVisualizationModeItem,
	FDSVisualizationModeSchema,
} from '@liferay/js-api/data-set';

interface IClientExtensionViewProps {
	items: Array<FDSVisualizationModeItem>;
	onItemSelectionChange: (
		item: FDSVisualizationModeItem,
		forceSingleSelection: boolean
	) => void;
	schema?: FDSVisualizationModeSchema;
	visualizationMode?: FDSVisualizationMode;
}

function logError(
	visualizationMode: FDSVisualizationMode | undefined,
	error: unknown
) {
	console.error(
		'The client extension implemented by the function',
		visualizationMode,
		'caused an error when trying to render its HTML content.',
		'Please fix your client extension.',
		error
	);
}

export default function ClientExtensionView({
	items,
	onItemSelectionChange,
	schema,
	visualizationMode,
}: IClientExtensionViewProps) {
	const {
		loadData,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
		selectionType,
	}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

	const containerRef = useRef<HTMLDivElement>(null);
	const [error, setError] = useState(false);
	const instanceRef = useRef<FDSVisualizationModeInstance | null>(null);
	const loadDataRef = useRef(loadData);
	const onItemSelectionChangeRef = useRef(onItemSelectionChange);

	useEffect(() => {
		loadDataRef.current = loadData;
		onItemSelectionChangeRef.current = onItemSelectionChange;
	});

	const args = useMemo<FDSVisualizationModeArgs>(
		() => ({
			items,
			loadData: async () => {
				await loadDataRef.current();
			},
			schema,
			selection:
				selectable && selectionType
					? {
							itemsKey: selectedItemsKey,
							selectedValues: selectedItemsValue ?? [],
							toggleItem: (item) =>
								onItemSelectionChangeRef.current(
									item,
									selectionType === 'single'
								),
							type: selectionType,
						}
					: undefined,
		}),
		[
			items,
			schema,
			selectable,
			selectedItemsKey,
			selectedItemsValue,
			selectionType,
		]
	);

	useEffect(() => {
		const {current: container} = containerRef;

		if (!container || error) {
			return;
		}

		try {
			if (typeof visualizationMode !== 'function') {
				throw new Error(
					'The client extension does not export a visualization mode'
				);
			}

			if (instanceRef.current) {
				instanceRef.current.update(args);
			}
			else {
				instanceRef.current = visualizationMode(container, args);

				instanceRef.current.update(args);
			}
		}
		catch (error) {
			logError(visualizationMode, error);

			setError(true);
		}
	}, [args, error, visualizationMode]);

	useEffect(() => {
		const {current: container} = containerRef;

		return () => {
			const {current: instance} = instanceRef;

			instanceRef.current = null;

			try {
				instance?.destroy();
			}
			catch (error) {
				logError(visualizationMode, error);
			}

			container?.replaceChildren();
		};
	}, [error, visualizationMode]);

	return (
		<>
			{error && (
				<ClayAlert
					className="m-3"
					displayType="danger"
					title={Liferay.Language.get('error')}
				>
					{Liferay.Language.get(
						'this-visualization-mode-could-not-be-displayed'
					)}
				</ClayAlert>
			)}

			<div hidden={error} ref={containerRef} />
		</>
	);
}

export function getClientExtensionViewComponent(
	visualizationMode?: FDSVisualizationMode
) {
	return function VisualizationModeClientExtensionView(
		props: Omit<IClientExtensionViewProps, 'visualizationMode'>
	) {
		return (
			<ClientExtensionView
				{...props}
				visualizationMode={visualizationMode}
			/>
		);
	};
}
