/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {getAgentDefinitions} from '../agent_definition_form/services/AgentDefinitionService';
import {getChatbotDefinitions} from '../chatbot_form/services/ChatbotService';
import DashboardCard from './components/DashboardCard';
import DashboardCardSkeleton from './components/DashboardCardSkeleton';

interface Entry {
	active: boolean;
	description?: string;
	externalReferenceCode: string;
	title: string;
	workflowDefinitionName?: string;
}

interface HomeDashboardProps {
	agentBuilderURL: string;
	agentURL: string;
	backURL: string;
	chatbotURL: string;
	chatbotsURL: string;
}

function appendBackURL(url: string, backURL: string) {
	const params = new URLSearchParams({backURL});

	return `${url}?${params.toString()}`;
}

function appendEntryParams(
	url: string,
	externalReferenceCode: string,
	backURL: string,
	workflowDefinitionName?: string
) {
	const params = new URLSearchParams({backURL, externalReferenceCode});

	if (workflowDefinitionName) {
		params.set('workflowDefinitionName', workflowDefinitionName);
	}

	return `${url}?${params.toString()}`;
}

export default function HomeDashboard({
	agentBuilderURL,
	agentURL,
	backURL,
	chatbotURL,
	chatbotsURL,
}: HomeDashboardProps) {
	const [agents, setAgents] = useState<Entry[] | null>(null);
	const [chatbots, setChatbots] = useState<Entry[] | null>(null);
	const [agentsError, setAgentsError] = useState(false);
	const [chatbotsError, setChatbotsError] = useState(false);

	useEffect(() => {
		let isMounted = true;

		getAgentDefinitions({pageSize: '4', sort: 'dateModified:desc'})
			.then((data) => {
				if (isMounted) {
					setAgents(data?.items ?? []);
				}
			})
			.catch(() => {
				if (isMounted) {
					setAgentsError(true);
				}
			});

		getChatbotDefinitions({pageSize: '4', sort: 'dateModified:desc'})
			.then((data) => {
				if (isMounted) {
					setChatbots(data?.items ?? []);
				}
			})
			.catch(() => {
				if (isMounted) {
					setChatbotsError(true);
				}
			});

		return () => {
			isMounted = false;
		};
	}, []);

	return (
		<div className="home-dashboard">
			<div className="home-dashboard__hero">
				<h2 className="home-dashboard__hero-title">
					{Liferay.Language.get('start-building-ai-agents')}
				</h2>

				<p className="home-dashboard__hero-subtitle">
					{Liferay.Language.get(
						'create-ai-agents-that-help-teams-and-customers-work-more-efficiently'
					)}
				</p>

				<a
					className="btn btn-primary home-dashboard__hero-btn rounded-pill"
					href={appendBackURL(agentURL, backURL)}
				>
					{Liferay.Language.get('create-new-agent')}
				</a>
			</div>

			<HomeDashboardSection
				emptyLabel={Liferay.Language.get('no-agents-were-found')}
				errorLabel={Liferay.Language.get('failed-to-load-agents')}
				hasError={agentsError}
				items={agents}
				link={{
					label: Liferay.Language.get('view-agents'),
					url: agentBuilderURL,
				}}
				renderItem={(item) => (
					<DashboardCard
						active={item.active !== false}
						category={Liferay.Language.get('agents')}
						description={item.description ?? ''}
						detailURL={appendEntryParams(
							agentURL,
							item.externalReferenceCode,
							backURL,
							item.workflowDefinitionName
						)}
						key={item.externalReferenceCode}
						title={item.title}
					/>
				)}
				take={4}
				title={Liferay.Language.get('latest-agents')}
			/>

			<HomeDashboardSection
				emptyLabel={Liferay.Language.get('no-chatbots-were-found')}
				errorLabel={Liferay.Language.get('failed-to-load-chatbots')}
				hasError={chatbotsError}
				items={chatbots}
				link={{
					label: Liferay.Language.get('view-chatbots'),
					url: chatbotsURL,
				}}
				renderItem={(item) => (
					<DashboardCard
						active={item.active !== false}
						category={Liferay.Language.get('chatbots')}
						description={item.description ?? ''}
						detailURL={appendEntryParams(
							chatbotURL,
							item.externalReferenceCode,
							backURL
						)}
						key={item.externalReferenceCode}
						title={item.title}
					/>
				)}
				take={4}
				title={Liferay.Language.get('latest-chatbots')}
			/>
		</div>
	);
}

interface HomeDashboardSectionProps {
	emptyLabel: string;
	errorLabel: string;
	hasError: boolean;
	items: Entry[] | null;
	link: {label: string; url: string};
	renderItem: (item: Entry) => React.ReactNode;
	take: number;
	title: string;
}

function HomeDashboardSection({
	emptyLabel,
	errorLabel,
	hasError,
	items,
	link,
	renderItem,
	take,
	title,
}: HomeDashboardSectionProps) {
	return (
		<section className="home-dashboard__section">
			<header className="home-dashboard__section-header">
				<h3 className="home-dashboard__section-title">{title}</h3>

				<a className="home-dashboard__section-link" href={link.url}>
					{link.label}
				</a>
			</header>

			{hasError ? (
				<p className="home-dashboard__section-message text-secondary">
					{errorLabel}
				</p>
			) : items === null ? (
				<div className="home-dashboard__cards">
					{Array.from({length: take}).map((_, index) => (
						<DashboardCardSkeleton key={index} />
					))}
				</div>
			) : !items.length ? (
				<p className="home-dashboard__section-message text-secondary">
					{emptyLabel}
				</p>
			) : (
				<div className="home-dashboard__cards">
					{items.slice(0, take).map(renderItem)}
				</div>
			)}
		</section>
	);
}
