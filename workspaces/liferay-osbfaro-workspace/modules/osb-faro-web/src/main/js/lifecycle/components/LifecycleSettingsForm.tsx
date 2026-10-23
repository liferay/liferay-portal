import BaseEditPage from 'shared/components/base-edit-page';
import ClayAlert from '@clayui/alert';
import LifecycleConditionsPanel from 'lifecycle/components/LifecycleConditionsPanel';
import LifecycleSummary from 'lifecycle/components/LifecycleSummary';
import NavigationWarning from 'shared/components/NavigationWarning';
import React, {useEffect, useRef} from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import {getPluralMessage} from 'shared/util/lang';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';

interface ILifecycleSettingsFormProps {
	authorName: string;
	form: ReturnType<typeof useLifecycleSettingsForm>;
	modifiedDate?: number | null;
	onSubmit: () => void;
	submitLabel: string;
	title: string;
}

const LifecycleSettingsForm: React.FC<ILifecycleSettingsFormProps> = ({
	authorName,
	form,
	modifiedDate,
	onSubmit,
	submitLabel,
	title,
}) => {
	const {
		addStage,
		alertCount,
		alertMessages,
		blurField,
		catalogFields,
		changed,
		dismissAlert,
		duplicateStageKeys,
		isFieldErrorVisible,
		lifecycleDescription,
		lifecycleName,
		lifecycleURL,
		moveStage,
		removeStage,
		selectedStage,
		setLifecycleDescription,
		setLifecycleName,
		setSelectedKey,
		stageConfigs,
		stageErrorKeys,
		summaryError,
		updateStage,
	} = form;

	const alertRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		if (alertCount) {
			alertRef.current?.focus();
		}
	}, [alertCount]);

	return (
		<BaseEditPage className="d-flex flex-column" documentTitle={title}>
			<NavigationWarning when={changed} />

			<BaseEditPage.Toolbar backURL={lifecycleURL} title={title}>
				<BaseEditPage.Toolbar.Cancel href={lifecycleURL} />

				<BaseEditPage.Toolbar.Save
					label={submitLabel}
					onClick={onSubmit}
				/>
			</BaseEditPage.Toolbar>

			<BaseEditPage.Body>
				<BaseEditPage.Sidebar
					label={Liferay.Language.get('lifecycle-conditions')}
					position="left"
				>
					<LifecycleConditionsPanel
						lifecycleName={lifecycleName}
						onAddStage={addStage}
						onMoveStage={moveStage}
						onRemoveStage={removeStage}
						onSelect={setSelectedKey}
						selectedKey={selectedStage?.key ?? null}
						stageConfigs={stageConfigs}
						stageErrorKeys={stageErrorKeys}
						summaryError={summaryError}
					/>
				</BaseEditPage.Sidebar>

				<BaseEditPage.Main className="page-container">
					{!!alertMessages.length && (
						<div className="mb-4" ref={alertRef} tabIndex={-1}>
							<ClayAlert
								displayType="danger"
								onClose={dismissAlert}
								title={
									getPluralMessage(
										Liferay.Language.get(
											'review-1-error-in-this-configuration'
										),
										Liferay.Language.get(
											'review-x-errors-in-this-configuration'
										),
										alertMessages.length,
										true
									) as string
								}
							>
								<ul className="mb-0 mt-1">
									{alertMessages.map((message) => (
										<li key={message}>{message}</li>
									))}
								</ul>
							</ClayAlert>
						</div>
					)}

					{selectedStage ? (
						<StageConfigurationPanel
							fields={catalogFields?.items}
							isFieldErrorVisible={isFieldErrorVisible}
							key={selectedStage.key}
							lifecycleName={lifecycleName}
							nameTaken={duplicateStageKeys.includes(
								selectedStage.key
							)}
							onChange={(value) =>
								updateStage(selectedStage.key, value)
							}
							onFieldBlur={blurField}
							onSelectSummary={() => setSelectedKey(null)}
							value={selectedStage}
						/>
					) : (
						<LifecycleSummary
							authorName={authorName}
							description={lifecycleDescription}
							isFieldErrorVisible={isFieldErrorVisible}
							modifiedDate={modifiedDate}
							name={lifecycleName}
							onDescriptionChange={setLifecycleDescription}
							onFieldBlur={blurField}
							onNameChange={setLifecycleName}
							stageCount={stageConfigs.length}
						/>
					)}
				</BaseEditPage.Main>
			</BaseEditPage.Body>
		</BaseEditPage>
	);
};

export default LifecycleSettingsForm;
