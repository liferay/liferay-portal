/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ArrowHeadType, Node, Position, XYPosition} from 'react-flow-renderer';

export function checkPostalAddressUnsupportedObjectRelationship(
	nodes: Node<ObjectDefinitionNodeData>[],
	sourceNode: Node<ObjectDefinitionNodeData>,
	targetNode: Node<ObjectDefinitionNodeData>
) {
	const isTargetNodePostalAddress =
		targetNode.data?.dbTableName === 'Address';

	if (isTargetNodePostalAddress) {
		return true;
	}

	const accountObjectDefinition = nodes.find(
		(node) => node.data?.name === 'AccountEntry'
	) as Node<ObjectDefinitionNodeData>;

	const targetHasAccountRelationship =
		accountObjectDefinition &&
		accountObjectDefinition.data?.objectRelationships.some(
			(objectRelationship) => {
				return (
					objectRelationship.objectDefinitionExternalReferenceCode2 ===
						targetNode.data?.externalReferenceCode &&
					objectRelationship.type === 'oneToMany'
				);
			}
		);

	const isSourceNodePostalAddress =
		sourceNode.data?.dbTableName === 'Address';

	return isSourceNodePostalAddress && !targetHasAccountRelationship;
}

// this helper function returns the intersection point
// of the line between the center of the intersectionNode and the target node

export function createElements() {
	const elements = [];
	const center = {x: window.innerWidth / 2, y: window.innerHeight / 2};

	elements.push({
		data: {label: 'Target'},
		id: 'target',
		position: center,
	});

	for (let i = 0; i < 8; i++) {
		const degrees = i * (360 / 8);
		const radians = degrees * (Math.PI / 180);
		const x = 250 * Math.cos(radians) + center.x;
		const y = 250 * Math.sin(radians) + center.y;

		elements.push({
			data: {label: 'Source'},
			id: `${i}`,
			position: {x, y},
		});

		elements.push({
			arrowHeadType: ArrowHeadType.Arrow,
			id: `edge-${i}`,
			source: `${i}`,
			target: 'target',
			type: 'floating',
		});
	}

	return elements;
}

export function getEdgeParams(source: Node, target: Node) {
	const sourceIntersectionPoint = getNodeIntersection(source, target);

	const targetIntersectionPoint = getNodeIntersection(target, source);

	const sourcePos = getEdgePosition(sourceIntersectionPoint, source);

	const targetPos = getEdgePosition(targetIntersectionPoint, target);

	return {
		sourcePos,
		sourceX: sourceIntersectionPoint.x,
		sourceY: sourceIntersectionPoint.y,
		targetPos,
		targetX: targetIntersectionPoint.x,
		targetY: targetIntersectionPoint.y,
	};
}

function getEdgePosition(intersectionPoint: XYPosition, node: Node) {
	const nodeProperties = {...node.__rf.position, ...node.__rf};
	const nodePositionX = Math.round(nodeProperties.x);
	const nodePositionY = Math.round(nodeProperties.y);
	const intersectionPointX = Math.round(intersectionPoint.x);
	const intersectionPointY = Math.round(intersectionPoint.y);

	if (intersectionPointX <= nodePositionX + 1) {
		return Position.Left;
	}
	if (intersectionPointX >= nodePositionX + nodeProperties.width - 1) {
		return Position.Right;
	}
	if (intersectionPointY <= nodePositionY + 1) {
		return Position.Top;
	}
	if (intersectionPointY >= nodeProperties.y + nodeProperties.height - 1) {
		return Position.Bottom;
	}

	return Position.Top;
}

function getNodeIntersection(
	intersectionNode: Node,
	targetNode: Node
): XYPosition {

	// https://math.stackexchange.com/questions/1724792/an-algorithm-for-finding-the-intersection-point-between-a-center-of-vision-and-a

	const {
		height: intersectionNodeHeight,
		position: intersectionNodePosition,
		width: intersectionNodeWidth,
	} = intersectionNode.__rf;
	const targetPosition = targetNode.__rf.position;

	const nodeHalfWidth = intersectionNodeWidth / 2;
	const nodeHalfHeight = intersectionNodeHeight / 2;

	const sourceCoordinateX = intersectionNodePosition.x + nodeHalfWidth;
	const sourceCoordinateY = intersectionNodePosition.y + nodeHalfHeight;
	const targetCoordinateX = targetPosition.x + nodeHalfWidth;
	const targetCoordinateY = targetPosition.y + nodeHalfHeight;

	const sourceToTargetXDifference =
		(targetCoordinateX - sourceCoordinateX) / (2 * nodeHalfWidth) -
		(targetCoordinateY - sourceCoordinateY) / (2 * nodeHalfHeight);
	const sourceToTargetYDifference =
		(targetCoordinateX - sourceCoordinateX) / (2 * nodeHalfWidth) +
		(targetCoordinateY - sourceCoordinateY) / (2 * nodeHalfHeight);

	const normalizedScale =
		1 /
		(Math.abs(sourceToTargetXDifference) +
			Math.abs(sourceToTargetYDifference));

	const normalizedSourceToTargetXDifference =
		normalizedScale * sourceToTargetXDifference;
	const normalizedSourceToTargetYDifference =
		normalizedScale * sourceToTargetYDifference;

	const intersectionPointX =
		nodeHalfWidth *
			(normalizedSourceToTargetXDifference +
				normalizedSourceToTargetYDifference) +
		sourceCoordinateX;
	const intersectionPointY =
		nodeHalfHeight *
			(-normalizedSourceToTargetXDifference +
				normalizedSourceToTargetYDifference) +
		sourceCoordinateY;

	return {x: intersectionPointX, y: intersectionPointY};
}

export function getObjectFolderName(): string {
	const urlSearchParams = new URLSearchParams(window.location.search);

	return urlSearchParams.get('objectFolderName') || '';
}

function hasPositionedNode(objectFolderItems: ObjectFolderItem[]) {
	return objectFolderItems.some(
		(objectFolderItem) =>
			objectFolderItem.positionX !== 0 || objectFolderItem.positionY !== 0
	);
}

interface HandleUnplacedObjectDefinitionNode {
	index: number;
	objectFolderExternalReferenceCode: string;
	outdatedObjectFolderItems: ObjectFolderItem[];
	positionColumn: {x: number; y: number};
	updatedObjectFolderItems: ObjectFolderItem[];
}

function handleUnplacedObjectDefinitionNode({
	index,
	objectFolderExternalReferenceCode,
	outdatedObjectFolderItems,
	positionColumn,
	updatedObjectFolderItems,
}: HandleUnplacedObjectDefinitionNode) {
	const hasNewPositionedNode = hasPositionedNode(updatedObjectFolderItems);

	if (objectFolderExternalReferenceCode === 'default') {
		const hasOldPositionedNode = hasPositionedNode(
			outdatedObjectFolderItems
		);

		if (hasOldPositionedNode) {
			return getObjectDefinitionNodeNextPosition(
				outdatedObjectFolderItems
			);
		}

		return getDefaultPredefinedPosition(positionColumn, index);
	}
	else if (hasNewPositionedNode) {
		return getObjectDefinitionNodeNextPosition(updatedObjectFolderItems);
	}

	return getObjectFolderDiagramCenterPosition();
}

interface GetObjectDefinitionNodePosition {
	index: number;
	objectDefinition: ObjectDefinitionNodeData;
	objectFolderExternalReferenceCode: string;
	outdatedObjectFolderItems: ObjectFolderItem[];
	positionColumn: {x: number; y: number};
	updatedObjectFolderItems: ObjectFolderItem[];
}

export function getObjectDefinitionNodePosition({
	index,
	objectDefinition,
	objectFolderExternalReferenceCode,
	outdatedObjectFolderItems,
	positionColumn,
	updatedObjectFolderItems,
}: GetObjectDefinitionNodePosition) {
	const objectFolderItem = outdatedObjectFolderItems.find(
		(objectFolderItem) =>
			objectFolderItem.objectDefinitionExternalReferenceCode ===
			objectDefinition.externalReferenceCode
	);

	const {positionX, positionY} = objectFolderItem as ObjectFolderItem;

	if (positionX === 0 && positionY === 0) {
		return handleUnplacedObjectDefinitionNode({
			index,
			objectFolderExternalReferenceCode,
			outdatedObjectFolderItems,
			positionColumn,
			updatedObjectFolderItems,
		});
	}

	return {x: positionX, y: positionY};
}

export function getObjectDefinitionNodeNextPosition(
	objectFolderItems: ObjectFolderItem[]
) {
	const yPositions = objectFolderItems.map(
		(objectDefinitionNode) => objectDefinitionNode.positionY
	);
	const maximumY = Math.max(...yPositions);
	const maximumNodesYPosition = objectFolderItems.filter(
		(objectDefinitionNode) => objectDefinitionNode.positionY === maximumY
	);
	const xPositions = maximumNodesYPosition.map(
		(objectDefinitionNode) => objectDefinitionNode.positionX
	);
	const maximumX = Math.max(...xPositions);
	const mostBottomRightNodePosition = maximumNodesYPosition.find(
		(objectDefinitionNode) => objectDefinitionNode.positionX === maximumX
	);

	return {
		x: mostBottomRightNodePosition!.positionX + 380,
		y: mostBottomRightNodePosition!.positionY,
	};
}

export function getObjectFolderDiagramCenterPosition() {
	const diagramAreaPositionInfo = document
		.querySelector('.lfr-objects__model-builder-diagram-area')
		?.getBoundingClientRect();
	const objectDefinitionNodeContainerInfo = {height: 352, width: 284};

	if (diagramAreaPositionInfo) {
		return {
			x:
				diagramAreaPositionInfo.width / 2 -
				objectDefinitionNodeContainerInfo.width / 2,
			y:
				diagramAreaPositionInfo.height / 2 -
				objectDefinitionNodeContainerInfo.height / 2,
		};
	}
	else {
		return {x: 0, y: 0};
	}
}

function getDefaultPredefinedPosition(
	positionColumn: {x: number; y: number},
	index: number
) {
	const x = positionColumn.x * 380 + 360;
	const y = positionColumn.y * 450 + 100;

	positionColumn.x++;

	if ((index + 1) % 4 === 0 && index !== 0) {
		positionColumn.y++;
		positionColumn.x = 0;
	}

	return {x, y};
}

export function getUnsupportedObjectRelationshipErrorMessage(
	nodes: Node<ObjectDefinitionNodeData>[],
	sourceNode: Node<ObjectDefinitionNodeData>,
	targetNode: Node<ObjectDefinitionNodeData>
) {
	if (
		sourceNode.data?.modifiable === false &&
		targetNode.data?.modifiable === false
	) {
		return {
			errorMessage: Liferay.Language.get(
				'unmodifiable-system-objects-cannot-be-related'
			),
		};
	}
	if (sourceNode.data?.linkedObjectDefinition) {
		return {
			errorMessage: Liferay.Language.get(
				'adding-relationships-directly-to-linked-objects-is-not-allowed'
			),
		};
	}
	if (
		sourceNode.data?.storageType === 'salesforce' ||
		targetNode.data?.storageType === 'salesforce'
	) {
		return {
			errorMessage: Liferay.Language.get(
				'salesforce-objects-do-not-support-relationships'
			),
		};
	}
	if (
		checkPostalAddressUnsupportedObjectRelationship(
			nodes,
			sourceNode,
			targetNode
		)
	) {
		return {
			errorMessage: Liferay.Language.get(
				'postal-address-can-only-have-a-relationship-with-the-account-object'
			),
			learnMessage: 'accessing-accounts-data-from-custom-objects',
		};
	}
}

interface UpdatePreviousURLParam {
	paramType: string;
	paramURL: string;
	paramValue: string;
}

export function updatePreviousURLParam({
	paramType,
	paramURL,
	paramValue,
}: UpdatePreviousURLParam) {
	const newPreviousURL = new URL(paramURL);

	newPreviousURL.searchParams.set(paramType, paramValue);

	window.location.href = newPreviousURL.toString();
}
