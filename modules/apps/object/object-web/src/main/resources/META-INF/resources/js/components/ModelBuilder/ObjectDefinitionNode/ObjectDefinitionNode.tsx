/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {stringUtils} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import React, {useRef} from 'react';
import {Handle, NodeProps, Position, useStore} from 'react-flow-renderer';

import {getObjectDefinitionNodeActions} from '../../ViewObjectDefinitions/objectDefinitionUtil';
import {useObjectFolderContext} from '../ModelBuilderContext/objectFolderContext';
import {TYPES} from '../ModelBuilderContext/typesEnum';
import {ObjectDefinitionNodeFields} from './ObjectDefinitionNodeFields';
import ObjectDefinitionNodeFooter from './ObjectDefinitionNodeFooter';
import ObjectDefinitionNodeHeader from './ObjectDefinitionNodeHeader';

import './NodeContainer.scss';

const selfRelationshipHandleStyle = {
	background: 'transparent',
	border: '2px transparent',
	borderRadius: '50%',
};
export function ObjectDefinitionNode({
	data: {
		dbTableName,
		defaultLanguageId,
		externalReferenceCode,
		hasObjectDefinitionDeleteResourcePermission,
		hasObjectDefinitionManagePermissionsResourcePermission,
		hasObjectDefinitionUpdateResourcePermission,
		id,
		label,
		linkedObjectDefinition,
		name,
		objectDefinitionSettings,
		objectFields,
		selected,
		showAllObjectFields,
		status,
		system,
	},
}: NodeProps<ObjectDefinitionNodeData>) {
	const [
		{
			baseResourceURL,
			nodeHandleConnectable,
			objectDefinitionPermissionsURL,
			objectFolders,
		},
		dispatch,
	] = useObjectFolderContext();

	const store = useStore();

	const nodeHandlePosition: {
		[key: string]: Position;
	} = {
		bottom: Position.Bottom,
		left: Position.Left,
		right: Position.Right,
		top: Position.Top,
	};

	const nodeHandleRefs: {
		[key: string]: React.RefObject<HTMLDivElement>;
	} = {
		bottom: useRef<HTMLDivElement>(null),
		left: useRef<HTMLDivElement>(null),
		right: useRef<HTMLDivElement>(null),
		top: useRef<HTMLDivElement>(null),
	};

	const displayNodeHandles = (display: boolean) => {
		for (const key in nodeHandleRefs) {
			const handleRef = nodeHandleRefs[key].current;

			if (handleRef) {
				handleRef.style.opacity = display ? '1' : '0';
			}
		}
	};

	const rootObjectDefinitionExternalReferenceCodes =
		objectDefinitionSettings?.find(
			(setting) =>
				setting.name === 'rootObjectDefinitionExternalReferenceCodes'
		)?.value;

	const isRootNode = !!rootObjectDefinitionExternalReferenceCodes
		?.split(',')
		.includes(externalReferenceCode);

	const isTreeStructure = !!rootObjectDefinitionExternalReferenceCodes;

	const isRootDescendantNode = isTreeStructure && !isRootNode;

	const handleSelectObjectDefinitionNode = () => {
		const {edges, nodes} = store.getState();

		dispatch({
			payload: {
				objectDefinitionNodes: nodes,
				objectRelationshipEdges: edges,
				selectedObjectDefinitionId: id.toString(),
			},
			type: TYPES.SET_SELECTED_OBJECT_DEFINITION_NODE,
		});
	};

	return (
		<>
			<div
				className={classNames(
					'lfr-objects__model-builder-node-container',
					{
						'lfr-objects__model-builder-node-container--link':
							linkedObjectDefinition,
						'lfr-objects__model-builder-node-container--selected':
							selected,
						'lfr-objects__model-builder-node-container--treeItem':
							isTreeStructure,
					}
				)}
				onMouseEnter={() => {
					displayNodeHandles(true);
				}}
				onMouseLeave={() => {
					displayNodeHandles(false);
				}}
			>
				<ObjectDefinitionNodeHeader
					dbTableName={dbTableName}
					dropDownItems={getObjectDefinitionNodeActions({
						baseResourceURL,
						dispatch,
						hasObjectDefinitionDeleteResourcePermission,
						hasObjectDefinitionManagePermissionsResourcePermission,
						hasObjectDefinitionUpdateResourcePermission,
						isTreeStructure,
						objectDefinitionId: id,
						objectDefinitionName: name,
						objectDefinitionPermissionsURL,
						objectFoldersLength: objectFolders.length,
						status,
					})}
					handleSelectObjectDefinitionNode={
						handleSelectObjectDefinitionNode
					}
					isLinkedObjectDefinition={linkedObjectDefinition}
					isRootDescendantNode={isRootDescendantNode}
					isRootNode={isRootNode}
					objectDefinitionLabel={stringUtils.getLocalizableLabel({
						fallbackLabel: name,
						fallbackLanguageId: defaultLanguageId,
						labels: label,
					})}
					status={status!}
					system={system}
				/>

				<ObjectDefinitionNodeFields
					defaultLanguageId={defaultLanguageId}
					objectFields={objectFields}
					selectedObjectDefinitionId={id}
					showAllObjectFields={showAllObjectFields}
				/>

				<ObjectDefinitionNodeFooter
					externalReferenceCode={externalReferenceCode}
					handleSelectObjectDefinitionNode={
						handleSelectObjectDefinitionNode
					}
					isLinkedObjectDefinition={linkedObjectDefinition}
					showAllObjectFields={showAllObjectFields}
				/>

				<>
					{Object.keys(nodeHandleRefs).map((position) => (
						<Handle
							className="lfr-objects__model-builder-node-handle"
							id={`${id}_${position}`}
							key={`${id}_${position}`}
							position={nodeHandlePosition[position]}
							ref={nodeHandleRefs[position]}
							style={{
								background: '#80ACFF',
								height: '18px',
								opacity: 0,
								[position]: '-27px',
								width: '18px',
							}}
							type="source"
						/>
					))}
				</>
				<>
					<Handle
						className="lfr-objects__model-builder-node-handle"
						id={`${id}`}
						isConnectable={nodeHandleConnectable}
						key={`${id}`}
						position={Position.Bottom}
						style={{
							borderRadius: 0,
							bottom: '-27px',
							height: '410px',
							opacity: 0,
							width: '350px',
							zIndex: -1,
						}}
						type="target"
					/>

					<Handle
						className="lfr-objects__model-builder-node-handle"
						id="fixedLeftHandle"
						position={Position.Left}
						style={{
							...selfRelationshipHandleStyle,
							left: '10px',
							top: '50%',
						}}
						type="source"
					/>
					<Handle
						className="lfr-objects__model-builder-node-handle"
						id="fixedRightHandle"
						position={Position.Right}
						style={{
							...selfRelationshipHandleStyle,
							right: '4px',
							top: '50%',
						}}
						type="target"
					/>
				</>
			</div>
		</>
	);
}
