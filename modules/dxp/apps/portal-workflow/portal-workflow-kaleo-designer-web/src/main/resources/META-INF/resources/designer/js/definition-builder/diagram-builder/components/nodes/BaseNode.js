/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useRef} from 'react';
import {Handle} from 'react-flow-renderer';

import {DefinitionBuilderContext} from '../../../DefinitionBuilderContext';
import {defaultLanguageId} from '../../../constants';
import {singleEventObserver} from '../../../util/EventObserver';
import {DiagramBuilderContext} from '../../DiagramBuilderContext';
import {sourceHandles, targetHandles} from './nodeHandles';
import {nodeDescription} from './utils';

let connectionNodeId = '';
let handleConnect = false;

export default function BaseNode({
	actions,
	agentDefinitionExternalReferenceCode,
	assignments,
	className,
	description,
	descriptionSidebar,
	dragHandle,
	httpMethod,
	icon,
	id,
	inputVariables,
	isConnectable,
	isDragging,
	javaDelegate,
	label,
	newNode,
	nodeTypeClassName,
	notifications,
	outputVariables,
	prompt,
	rag,
	requestBody,
	script,
	scriptLanguage,
	sourcePosition,
	targetPosition,
	taskTimers,
	timeout,
	tools,
	type,
	url,
	userMessage,
	xPos,
	yPos,
	...otherProps
}) {
	const sourcehandlesRef = useRef();
	const targethandlesRef = useRef();
	const {selectedLanguageId} = useContext(DefinitionBuilderContext);

	const {
		collidingElements,
		selectedItem,
		setCollidingElements,
		setSelectedItem,
	} = useContext(DiagramBuilderContext);

	useEffect(() => {
		if (sourcehandlesRef?.current && targethandlesRef?.current) {
			const handleConnectEndSubscribe = singleEventObserver.subscribe(
				'handle-connect-end',
				() => {
					connectionNodeId = '';
					handleConnect = false;

					sourcehandlesRef.current.style.zIndex = '-1';
					targethandlesRef.current.style.zIndex = '-2';
				}
			);

			const handleConnectStartSubscribe = singleEventObserver.subscribe(
				'handle-connect-start',
				(nodeId) => {
					connectionNodeId = nodeId;
					handleConnect = true;

					if (id !== nodeId) {
						sourcehandlesRef.current.style.zIndex = '-2';
						targethandlesRef.current.style.zIndex = '-1';
					}
				}
			);

			return () => {
				singleEventObserver.unsubscribe(handleConnectEndSubscribe);
				singleEventObserver.unsubscribe(handleConnectStartSubscribe);
			};
		}
	});

	let borderAreaColor = 'blue';
	let displayBorderArea = false;

	if (collidingElements !== null && collidingElements.includes(id)) {
		borderAreaColor = 'red';
		displayBorderArea = true;
	}

	const descriptionColor = descriptionSidebar
		? 'text-secondary'
		: 'text-muted';

	if (!description) {
		description = nodeDescription[type];
	}

	if (selectedItem?.id === id) {
		nodeTypeClassName = `${nodeTypeClassName} selected`;
	}

	let nodeLabel = label[defaultLanguageId];

	if (selectedLanguageId && label[selectedLanguageId]) {
		nodeLabel = label[selectedLanguageId];
	}

	const displaySourceHandles = (display) => () => {
		if (sourcehandlesRef?.current && targethandlesRef?.current) {
			const handleRef = handleConnect
				? targethandlesRef
				: sourcehandlesRef;

			if (display && connectionNodeId !== id) {
				handleRef.current.style.opacity = '1';
			}
			else {
				targethandlesRef.current.style.opacity = '0';
				sourcehandlesRef.current.style.opacity = '0';
			}
		}
	};

	if (newNode) {
		setSelectedItem({
			data: {
				actions,
				agentDefinitionExternalReferenceCode,
				assignments,
				description,
				httpMethod,
				inputVariables,
				javaDelegate,
				label,
				newNode: false,
				notifications,
				outputVariables,
				prompt,
				rag,
				requestBody,
				script,
				scriptLanguage,
				taskTimers,
				timeout,
				tools,
				url,
				userMessage,
			},
			id,
			type,
		});
	}

	return (
		<div className={`base-node ${className}`}>
			{displayBorderArea && (
				<div className={`node-border-area ${borderAreaColor}`} />
			)}

			{!descriptionSidebar && (
				<div
					className="node-handle-area"
					onDragLeave={() => setCollidingElements(null)}
					onMouseEnter={displaySourceHandles(true)}
					onMouseLeave={displaySourceHandles(false)}
				>
					<div
						className="handles handles-default"
						ref={sourcehandlesRef}
					>
						{type !== 'end' &&
							sourceHandles.map((handle) => (
								<Handle
									id={handle.id}
									key={handle.id}
									position={handle.position}
									style={handle.style}
									type={handle.type}
								/>
							))}
					</div>

					<div className="handles" ref={targethandlesRef}>
						{type !== 'start' &&
							targetHandles.map((handle) => (
								<Handle
									id={handle.id}
									key={handle.id}
									position={handle.position}
									style={handle.style}
									type={handle.type}
								/>
							))}
					</div>
				</div>
			)}

			<div
				className={`node ${nodeTypeClassName}`}
				draghandle={dragHandle}
				isconnectable={isConnectable?.toString()}
				isdragging={isDragging?.toString()}
				onClick={() => {
					if (!descriptionSidebar) {
						setSelectedItem({
							data: {
								actions,
								agentDefinitionExternalReferenceCode,
								assignments,
								description,
								httpMethod,
								inputVariables,
								javaDelegate,
								label,
								notifications,
								outputVariables,
								prompt,
								rag,
								requestBody,
								script,
								scriptLanguage,
								taskTimers,
								timeout,
								tools,
								url,
								userMessage,
							},
							id,
							type,
						});
					}
				}}
				sourceposition={sourcePosition}
				style={{
					position: displayBorderArea ? 'absolute' : 'unset',
				}}
				targetposition={targetPosition}
				xpos={xPos}
				ypos={yPos}
				{...otherProps}
			>
				{descriptionSidebar && (
					<ClayIcon className="mr-4 text-secondary" symbol="drag" />
				)}

				<div className="mr-3 node-icon">
					<ClayIcon symbol={icon} />
				</div>

				<div className="node-info">
					<span className="node-label truncate-container">
						{nodeLabel}
					</span>

					<span
						className={`node-description truncate-container ${descriptionColor}`}
					>
						{descriptionSidebar ?? description}
					</span>
				</div>
			</div>
		</div>
	);
}

BaseNode.propTypes = {
	agentDefinitionExternalReferenceCode: PropTypes.string,
	className: PropTypes.string,
	description: PropTypes.string,
	descriptionSidebar: PropTypes.string,
	httpMethod: PropTypes.string,
	icon: PropTypes.string.isRequired,
	id: PropTypes.string,
	inputVariables: PropTypes.object,
	javaDelegate: PropTypes.string,
	label: PropTypes.object,
	nodeTypeClassName: PropTypes.string,
	outputVariables: PropTypes.object,
	prompt: PropTypes.string,
	rag: PropTypes.object,
	requestBody: PropTypes.string,
	timeout: PropTypes.string,
	tools: PropTypes.object,
	type: PropTypes.string.isRequired,
	url: PropTypes.string,
	userMessage: PropTypes.string,
};
