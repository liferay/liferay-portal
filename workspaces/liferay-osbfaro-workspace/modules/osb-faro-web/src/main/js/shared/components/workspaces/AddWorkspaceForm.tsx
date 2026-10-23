import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import ConfirmationModal from 'shared/components/ConfirmationModal';
import Constants, {FaroEnv} from 'shared/util/constants';
import Form, {
	validateMaxLength,
	validateMinLength,
	validatePattern,
	validateRequired,
} from 'shared/components/form';
import getCN from 'classnames';
import Loading, {Align} from 'shared/components/Loading';
import NavigationWarning from 'shared/components/NavigationWarning';
import React, {useContext, useRef, useState} from 'react';
import Sheet from 'shared/components/Sheet';
import TimeZonePicker from '../form/TimeZonePicker';
import URLConstants from 'shared/util/url-constants';
import {BasePageContext} from './BasePage';
import {Project, TimeZone} from 'shared/util/records';
import {sequence} from 'shared/util/promise';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {useModal} from '@clayui/modal';
import {
	validateEmail,
	validateEmailArr,
	validateEmailDomain,
	validateEmailDomainArr,
} from 'shared/util/email-validators';

const {
	faroURL,
	projectLocations: {AS1, EU2, EU3, SA, STG, US},
} = Constants;

const DEFAULT_TIME_ZONE = 'UTC';

const getProjectLocations = (): {label: string; value: string}[] => {
	switch (FARO_ENV) {
		case FaroEnv.Staging:
			return [
				{
					label: Liferay.Language.get('location-staging'),
					value: STG,
				},
			];
		default:
			return [
				{label: Liferay.Language.get('location-as1'), value: AS1},
				{label: Liferay.Language.get('location-eu'), value: EU2},
				{label: Liferay.Language.get('location-eu2'), value: EU3},
				{label: Liferay.Language.get('location-sa'), value: SA},
				{label: Liferay.Language.get('location-us'), value: US},
			];
	}
};

const getDefaultServerLocation = () => {
	switch (FARO_ENV) {
		case FaroEnv.Staging:
			return STG;
		default:
			return US;
	}
};

interface IAddWorkspaceFormProps extends React.HTMLAttributes<HTMLElement> {
	disabled: boolean;
	editing: boolean;
	emailAddressDomains: string[];
	onSubmit: (values: Record<string, any>) => Promise<any>;
	project?: Project;
}

const AddWorkspaceForm: React.FC<IAddWorkspaceFormProps> = ({
	className,
	disabled = false,
	editing = false,
	emailAddressDomains,
	onSubmit,
	project,
}) => {
	const {currentUser} = useContext(BasePageContext);

	const formRef = useRef<any>(null);

	const urlModal = useModal();

	const [pendingSubmit, setPendingSubmit] = useState<{
		friendlyURL: string;
		submit: () => Promise<any>;
	}>();

	const [inputListValue, setInputListValue] = useState<string>('');
	const [emailAddressesInputValues, setEmailAddressesInputValues] =
		useState<string>('');

	const handleSubmit = (
		values: Record<string, any>,
		{
			resetForm,
			setFieldError,
			setSubmitting,
		}: {
			resetForm: (args: {values: Record<string, any>}) => void;
			setFieldError: (field: string, message: string) => void;
			setSubmitting: (submitting: boolean) => void;
		}
	) => {
		const {initialValues} = formRef.current;
		const {friendlyURL: initialFriendlyURL} = initialValues;

		const {friendlyURL: newFriendlyURL} = values;

		const submitFn = () =>
			onSubmit({
				...values,
				ownerEmailAddress:
					project?.ownerEmailAddress || currentUser.emailAddress,
			})
				.then(() => {
					setSubmitting(false);

					if (initialFriendlyURL === newFriendlyURL) {
						resetForm({values});
					}
				})
				.catch(({field, message}) => {
					setSubmitting(false);

					if (field) {
						setFieldError(field, message);
					}
				});

		if (newFriendlyURL !== initialFriendlyURL) {
			setSubmitting(false);

			setPendingSubmit({
				friendlyURL: newFriendlyURL,
				submit: submitFn,
			});

			urlModal.onOpenChange(true);
		}
		else {
			submitFn();
		}
	};

	return (
		<div className={getCN('add-workspace-form-root', className)}>
			<Sheet>
				<Form
					enableReinitialize
					initialValues={{
						emailAddressDomains: emailAddressDomains || [],
						friendlyURL:
							project?.friendlyURL?.replace('/', '') || '',
						incidentReportEmailAddresses:
							project?.incidentReportEmailAddresses?.toArray() ||
							[],
						name: (project && project.name) || '',
						ownerEmailAddress:
							project?.ownerEmailAddress ||
							currentUser.emailAddress,
						serverLocation:
							project?.serverLocation ||
							getDefaultServerLocation(),
						timeZoneId:
							project?.getIn(['timeZone', 'timeZoneId']) ||
							DEFAULT_TIME_ZONE,
					}}
					innerRef={formRef as any}
					onSubmit={handleSubmit}
				>
					{({
						dirty,
						handleSubmit,
						initialValues,
						isSubmitting,
						isValid,
						resetForm,
						setFieldTouched,
						setFieldValue,
					}) => (
						<Form.Form onSubmit={handleSubmit}>
							<NavigationWarning when={!!project && dirty} />

							<Sheet.Header>
								<Sheet.Subtitle>
									{Liferay.Language.get('general')}
								</Sheet.Subtitle>

								<Sheet.Section className="input-name">
									<Form.Input
										disabled={disabled}
										label={Liferay.Language.get(
											'workspace-name'
										)}
										name="name"
										required
										validate={sequence([
											validateRequired,
											validateMaxLength(255),
										])}
									/>
								</Sheet.Section>

								<Sheet.Section className="input-workspace-owner-email">
									<Form.Input
										disabled
										label={Liferay.Language.get(
											'workspace-owner-email'
										)}
										name="ownerEmailAddress"
										required
									/>
								</Sheet.Section>

								<Sheet.Section className="input-server">
									<Form.Select
										data-testid="server-location-input"
										disabled={
											disabled ||
											editing ||
											(project && project.serverLocation)
										}
										label={Liferay.Language.get(
											'data-center-location'
										)}
										name="serverLocation"
										required
										secondaryInfo={Liferay.Language.get(
											'select-a-server-to-store-your-data.-this-could-have-implications-to-your-organizations-policy-on-user-data-storage'
										)}
									>
										{getProjectLocations().map(
											({label, value}) => (
												<Form.Select.Item
													key={value}
													value={value}
												>
													{label}
												</Form.Select.Item>
											)
										)}
									</Form.Select>
								</Sheet.Section>

								<Sheet.Section>
									<Form.Label>
										{Liferay.Language.get('timezone')}
									</Form.Label>

									<Text as="p" size={3}>
										{Liferay.Language.get(
											'time-zone-used-for-all-data-reporting-in-this-workspace.-it-is-automatically-set-based-on-your-time-zone-and-cannot-be-changed'
										)}
									</Text>

									<TimeZonePicker
										disabled
										fieldName="timeZoneId"
										initialTimeZone={
											project
												? new TimeZone(
														project.getIn([
															'timeZone',
														])
													)
												: new TimeZone()
										}
										setFieldTouched={setFieldTouched}
										setFieldValue={setFieldValue}
									/>
								</Sheet.Section>

								<Sheet.Section>
									<div>
										<Text size={3} weight="semi-bold">
											{Liferay.Language.get(
												'set-a-friendly-workspace-url'
											)}
										</Text>
									</div>

									<Text size={3}>
										{Liferay.Language.get(
											'define-a-friendly-url-that-others-can-use-to-access-and-share-this-workspace.-this-value-cannot-be-changed-after-it-is-set'
										)}
									</Text>

									<div className="mb-1">
										<Text color="secondary" size={3}>
											{sub(
												Liferay.Language.get('e.g.-x'),
												[
													<React.Fragment key="WORKSPACE_URL">
														<span>{faroURL}</span>
														<strong>
															{'/workspace-name'}
														</strong>
													</React.Fragment>,
												],
												false
											)}
										</Text>
									</div>

									<Form.Input
										data-testid="friendly-url-input"
										disabled={
											disabled ||
											(project && project.friendlyURL)
										}
										name="friendlyURL"
										text={{
											content: '/',
											position: 'prepend',
										}}
										validate={sequence([
											validateMinLength(2),
											validateMaxLength(255),
											validatePattern(
												/^(?=.*[a-z])[a-z0-9._-]+$/,
												sub(
													Liferay.Language.get(
														'workspace-url-must-only-contain-x-and-at-least-one-letter'
													),
													["a-z, 0-9, '.', '_', '-'"]
												) as string
											),
										])}
									/>
								</Sheet.Section>

								<Sheet.Section>
									<div>
										<Text size={3} weight="semi-bold">
											{Liferay.Language.get(
												'allowed-email-domains'
											)}
										</Text>
									</div>

									<Text size={3}>
										{Liferay.Language.get(
											'define-which-email-domains-can-request-access-to-this-workspace'
										)}
									</Text>

									<div className="mb-1">
										<Text color="secondary" size={3}>
											{sub(
												Liferay.Language.get('e.g.-x'),
												[
													<React.Fragment key="EMAIL_DOMAIN">
														<span>
															{'user.name@'}
														</span>
														<strong>
															{
																'company-domain.com'
															}
														</strong>
													</React.Fragment>,
												],
												false
											)}
										</Text>
									</div>

									<Form.InputList
										disabled={disabled}
										errorMessage={Liferay.Language.get(
											'please-enter-the-domain-in-this-format-domain-com'
										)}
										name="emailAddressDomains"
										onChangeInputList={setInputListValue}
										text={{
											content: '@',
											position: 'prepend',
										}}
										validate={(items: string[]) =>
											validateEmailDomainArr(
												items,
												inputListValue
											)
										}
										validationFn={validateEmailDomain}
									/>
								</Sheet.Section>

								<Sheet.Subtitle>
									{Liferay.Language.get('security')}
								</Sheet.Subtitle>

								<Sheet.Section>
									<Form.InputList
										errorMessage={Liferay.Language.get(
											'please-enter-the-email-in-this-format-sample-email-com'
										)}
										label={Liferay.Language.get(
											'add-incident-report-contacts'
										)}
										name="incidentReportEmailAddresses"
										onChangeInputList={
											setEmailAddressesInputValues
										}
										popover={{
											content: (
												<div className="add-workspace-popover-content">
													{Liferay.Language.get(
														'this-person-will-be-contacted-in-the-event-of'
													)}

													<ul>
														<li>
															{Liferay.Language.get(
																'service-interruptions'
															).toLowerCase()}
														</li>

														<li>
															{Liferay.Language.get(
																'security-incidents'
															).toLowerCase()}
														</li>

														<li>
															{Liferay.Language.get(
																'other-urgent-service-updates-that-require-action'
															).toLowerCase()}
														</li>
													</ul>
												</div>
											),
											title: Liferay.Language.get(
												'incident-report-contact'
											),
										}}
										required
										secondaryInfo={Liferay.Language.get(
											'who-should-we-contact-in-case-of-a-security-breach'
										)}
										validate={sequence([
											validateRequired,
											(items) =>
												validateEmailArr(
													items,
													emailAddressesInputValues
												),
										])}
										validationFn={validateEmail}
									/>
								</Sheet.Section>
							</Sheet.Header>

							<Sheet.Footer divider={false}>
								{!editing ? (
									<>
										<div className="terms">
											<Form.Checkbox
												label={Liferay.Language.get(
													'i-agree'
												)}
												name="termsAcceptance"
												validate={validateRequired}
											/>

											<p>
												{sub(
													Liferay.Language.get(
														'by-selecting-i-agree-,-you-agree-to-our-x-including-our-x'
													),
													[
														<ClayLink
															href={
																URLConstants.TermsAndConditions
															}
															key="'terms-and-conditions'"
														>
															{Liferay.Language.get(
																'terms-and-conditions'
															)}
														</ClayLink>,
														<ClayLink
															href={
																URLConstants.PrivacyPolicy
															}
															key="privacy-policy"
														>
															{Liferay.Language.get(
																'privacy-policy'
															)}
														</ClayLink>,
													],
													false
												)}
											</p>
										</div>

										<ClayButton
											block
											className="button-root"
											disabled={
												disabled ||
												isSubmitting ||
												!isValid
											}
											displayType="primary"
											type="submit"
										>
											{isSubmitting && (
												<Loading align={Align.Left} />
											)}

											{Liferay.Language.get(
												'finish-setup'
											)}
										</ClayButton>
									</>
								) : (
									<>
										<ClayButton
											className="button-root mr-3"
											disabled={
												disabled ||
												isSubmitting ||
												!isValid
											}
											displayType="primary"
											type="submit"
										>
											{isSubmitting && (
												<Loading align={Align.Left} />
											)}

											{Liferay.Language.get('save')}
										</ClayButton>

										<ClayButton
											className="button-root"
											disabled={disabled || !dirty}
											displayType="secondary"
											onClick={() =>
												resetForm({
													values: initialValues,
												})
											}
										>
											{Liferay.Language.get('cancel')}
										</ClayButton>
									</>
								)}
							</Sheet.Footer>
						</Form.Form>
					)}
				</Form>
			</Sheet>

			{urlModal.open && pendingSubmit && (
				<ConfirmationModal
					message={
						<div>
							<p className="text-secondary">
								{Liferay.Language.get(
									'you-can-only-set-your-friendly-workspace-url-once.-are-you-sure-you-would-like-to-save-it-as-the-following-url'
								)}
							</p>

							<p>
								<span className="text-secondary">
									{`${faroURL}/workspace-name/`}
								</span>

								<b>{pendingSubmit.friendlyURL}</b>
							</p>
						</div>
					}
					observer={urlModal.observer}
					onClose={() => urlModal.onOpenChange(false)}
					onSubmit={() => pendingSubmit.submit()}
					submitMessage={
						editing
							? Liferay.Language.get('save')
							: Liferay.Language.get('create-workspace')
					}
					title={Liferay.Language.get(
						'setting-friendly-workspace-url'
					)}
					type="info"
				/>
			)}
		</div>
	);
};

export default AddWorkspaceForm;
