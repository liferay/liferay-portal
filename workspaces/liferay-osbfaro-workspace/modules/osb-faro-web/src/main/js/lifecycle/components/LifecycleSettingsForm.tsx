import BaseEditPage from 'shared/components/base-edit-page';
import LifecycleConditionsPanel from 'lifecycle/components/LifecycleConditionsPanel';
import LifecycleErrorAlert from 'lifecycle/components/LifecycleErrorAlert';
import LifecycleSummary from 'lifecycle/components/LifecycleSummary';
import NavigationWarning from 'shared/components/NavigationWarning';
import React from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
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
		blurField,
		dismissAlert,
		moveStage,
		removeStage,
		selectedStage,
		setLifecycleDescription,
		setLifecycleName,
		setSelectedKey,
	} = form;

	return (
		<BaseEditPage className="d-flex flex-column" documentTitle={title}>
			<NavigationWarning when={form.changed} />

			<BaseEditPage.Toolbar backURL={form.lifecycleURL} title={title}>
				<BaseEditPage.Toolbar.Cancel href={form.lifecycleURL} />

				<BaseEditPage.Toolbar.Save
					disabled={form.saving}
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
						lifecycleName={form.lifecycleName}
						onAddStage={addStage}
						onMoveStage={moveStage}
						onRemoveStage={removeStage}
						onSelect={setSelectedKey}
						selectedKey={selectedStage?.key ?? null}
						stageConfigs={form.stageConfigs}
						stageErrorKeys={form.stageErrorKeys}
						summaryError={form.summaryError}
					/>
				</BaseEditPage.Sidebar>

				<BaseEditPage.Main className="page-container">
					<LifecycleErrorAlert
						alertCount={form.alertCount}
						messages={form.alertMessages}
						onClose={dismissAlert}
					/>

					{selectedStage ? (
						<StageConfigurationPanel
							fields={form.catalogFields?.items}
							isFieldErrorVisible={form.isFieldErrorVisible}
							key={selectedStage.key}
							lifecycleName={form.lifecycleName}
							nameTaken={form.duplicateStageKeys.includes(
								selectedStage.key
							)}
							onChange={(value) =>
								form.updateStage(selectedStage.key, value)
							}
							onFieldBlur={blurField}
							onSelectSummary={() => setSelectedKey(null)}
							value={selectedStage}
						/>
					) : (
						<LifecycleSummary
							authorName={authorName}
							description={form.lifecycleDescription}
							isFieldErrorVisible={form.isFieldErrorVisible}
							modifiedDate={modifiedDate}
							name={form.lifecycleName}
							onDescriptionChange={setLifecycleDescription}
							onFieldBlur={blurField}
							onNameChange={setLifecycleName}
							stageCount={form.stageConfigs.length}
						/>
					)}
				</BaseEditPage.Main>
			</BaseEditPage.Body>
		</BaseEditPage>
	);
};

export default LifecycleSettingsForm;
