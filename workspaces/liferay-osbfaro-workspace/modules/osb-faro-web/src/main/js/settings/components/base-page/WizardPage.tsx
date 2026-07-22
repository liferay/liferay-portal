import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import React, {useEffect, useState} from 'react';
import Toolbar from './Toolbar';
import URLConstants from 'shared/util/url-constants';
import {addAlert} from 'shared/actions/alerts';
import {close, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {Heading, Text} from '@clayui/core';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useParams} from 'react-router-dom';
import {useQueryParams} from 'shared/hooks/useQueryParams';
import {useWizardPage, WizardPageProvider} from './WizardPageContext';

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IWizardStepsContentProps extends PropsFromRedux {
	groupId: string;
	onNext: () => void;
	onPrev: () => void;
}

export type Step = {
	content: React.FC<IWizardStepsContentProps>;
	description: string;
	title: string;
};

function getSafeStepFromURL(steps: Step[], initStep: string | null) {
	const step = Number(initStep);

	if (!step || step < 0) {
		return 0;
	}

	if (step >= steps.length) {
		return steps.length - 1;
	}

	return step;
}

function updateSearchParams(
	history: ReturnType<typeof useHistoryAdapter>,
	key: string,
	value: any
) {
	const params = new URLSearchParams(window.location.search);
	params.set(key, String(value));

	history.push({
		pathname: window.location.pathname,
		search: params.toString(),
	});
}

interface IWizardStepsProps extends PropsFromRedux {
	steps: Step[];
}

const WizardSteps = ({addAlert, close, open, steps}: IWizardStepsProps) => {
	const {groupId = ''} = useParams<{groupId: string}>();
	const history = useHistoryAdapter();
	const params = useQueryParams();
	const {loadingContext, refetchDataSource} = useWizardPage();

	const [stepIndex, setStepIndex] = useState(
		getSafeStepFromURL(steps, params.stepIndex)
	);

	const currentStep = steps[stepIndex];

	const handleSetStep = async (newStepIndex: number) => {
		updateSearchParams(history, 'stepIndex', newStepIndex);

		if (params.dataSourceId) {
			refetchDataSource(params.dataSourceId);
		}

		setStepIndex(newStepIndex);
	};

	return (
		<div className="w-100">
			<Text color="secondary" size={3}>
				{sub(Liferay.Language.get('step-x-of-x'), [
					stepIndex + 1,
					steps.length,
				])}
			</Text>

			<div className="mb-3 mt-2">
				<Heading level={4} weight="bold">
					{currentStep.title}
				</Heading>
			</div>

			<div className="mb-1">
				<Text color="secondary" size={4}>
					{currentStep.description}
				</Text>
			</div>

			<ClayLink
				decoration="underline"
				href={URLConstants.HelpConnectDxp}
				target="_blank"
			>
				<Text size={4} weight="semi-bold">
					{Liferay.Language.get('learn-more-about-data-sources')}
				</Text>

				<ClayIcon
					aria-label={Liferay.Language.get(
						'learn-more-about-data-sources'
					)}
					className="ml-1"
					fontSize={12}
					symbol="shortcut"
				/>
			</ClayLink>

			<div className="mt-5">
				{!loadingContext && (
					<currentStep.content
						addAlert={addAlert}
						close={close}
						groupId={groupId}
						onNext={() => {
							if (stepIndex < steps.length - 1) {
								handleSetStep(stepIndex + 1);
							}
						}}
						onPrev={() => {
							if (stepIndex > 0) {
								handleSetStep(stepIndex - 1);
							}
						}}
						open={open}
					/>
				)}
			</div>
		</div>
	);
};

const connector = connect(null, {
	addAlert,
	close,
	open,
});

const WizardPage = ({children}: {children: React.ReactNode}) => {
	const {groupId = ''} = useParams<{groupId: string}>();

	// TODO: Trick to add background white on wizad mode, remove it on revamping.

	useEffect(() => {
		const bodyElement = document.querySelector('body');
		if (bodyElement instanceof HTMLElement) {
			bodyElement.style.backgroundColor = 'white';
		}

		return () => {
			if (bodyElement instanceof HTMLElement)
				bodyElement.style.backgroundColor = '';
		};
	}, []);

	return (
		<WizardPageProvider>
			<div className="wizard-page">
				<Toolbar
					backURL={{
						label: Liferay.Language.get('data-sources'),
						url: toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId,
						}),
					}}
				/>

				<ClayLayout.Container fluid>
					<ClayLayout.Row>
						<ClayLayout.Col md={6} sm={12}>
							<div className="wizard-page__content-col">
								{children}
							</div>
						</ClayLayout.Col>
						<ClayLayout.Col size={6}>
							<div className="wizard-page__onboarding-col">
								<div className="wizard-page__onboarding-image" />
							</div>
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayLayout.Container>
			</div>
		</WizardPageProvider>
	);
};

export default connector((props: any) => (
	<WizardPage>
		<WizardSteps {...props} />
	</WizardPage>
));
