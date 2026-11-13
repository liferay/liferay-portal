import BaseEditPage from 'shared/components/base-edit-page';
import ClayAlert from '@clayui/alert';
import LifecycleConditionsPanel from 'lifecycle/components/LifecycleConditionsPanel';
import LifecycleErrorAlert from 'lifecycle/components/LifecycleErrorAlert';
import LifecycleSummary from 'lifecycle/components/LifecycleSummary';
import NavigationWarning from 'shared/components/NavigationWarning';
import React from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import {isAtStageLimit} from 'lifecycle/utils/lifecycleValidation';
import {LIFECYCLE_STAGES_MAX_COUNT} from 'lifecycle/utils/constants';
import {sub} from 'shared/util/lang';
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

			<BaseEditPage.Toolbar backURL={form.backURL} title={title}>
				<BaseEditPage.Toolbar.Cancel href={form.backURL} />

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
					{isAtStageLimit(form.stageConfigs) && (
						<ClayAlert
							className="mb-4"
							displayType="warning"
							title={Liferay.Language.get('warning')}
						>
							{
								sub(
									Liferay.Language.get(
										'you-have-reached-the-limit-of-x-stages-for-this-lifecycle'
									),
									[LIFECYCLE_STAGES_MAX_COUNT]
								) as string
							}
						</ClayAlert>
					)}

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
							nameTaken={form.nameTaken}
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
