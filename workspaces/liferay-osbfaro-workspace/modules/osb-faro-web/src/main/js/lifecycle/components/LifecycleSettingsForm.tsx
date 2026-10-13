import BaseEditPage from 'shared/components/base-edit-page';
import LifecycleConditionsPanel from 'lifecycle/components/LifecycleConditionsPanel';
import LifecycleSummary from 'lifecycle/components/LifecycleSummary';
import React from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import {ICatalogField} from 'shared/api/catalog';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';

interface ILifecycleSettingsFormProps {
	authorName: string;
	backURL: string;
	catalogFields?: ICatalogField[];
	lifecycleDescription: string;
	lifecycleName: string;
	modifiedDate?: number | null;
	onLifecycleDescriptionChange: (description: string) => void;
	onLifecycleNameChange: (name: string) => void;
	onSelect: (key: string | null) => void;
	onStageChange: (key: string, value: IStageConfig) => void;
	onSubmit: () => void;
	selectedStage: IStageConfig | null;
	stageConfigs: IStageConfig[];
	submitDisabled?: boolean;
	submitLabel: string;
	title: string;
}

const LifecycleSettingsForm: React.FC<ILifecycleSettingsFormProps> = ({
	authorName,
	backURL,
	catalogFields,
	lifecycleDescription,
	lifecycleName,
	modifiedDate,
	onLifecycleDescriptionChange,
	onLifecycleNameChange,
	onSelect,
	onStageChange,
	onSubmit,
	selectedStage,
	stageConfigs,
	submitDisabled = false,
	submitLabel,
	title,
}) => (
	<BaseEditPage className="d-flex flex-column" documentTitle={title}>
		<BaseEditPage.Toolbar backURL={backURL} title={title}>
			<BaseEditPage.Toolbar.Cancel href={backURL} />

			<BaseEditPage.Toolbar.Save
				disabled={submitDisabled}
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
					onSelect={onSelect}
					selectedKey={selectedStage?.key ?? null}
					stageConfigs={stageConfigs}
				/>
			</BaseEditPage.Sidebar>

			<BaseEditPage.Main className="page-container">
				{selectedStage ? (
					<StageConfigurationPanel
						fields={catalogFields}
						key={selectedStage.key}
						lifecycleName={lifecycleName}
						onChange={(value) =>
							onStageChange(selectedStage.key, value)
						}
						onSelectSummary={() => onSelect(null)}
						value={selectedStage}
					/>
				) : (
					<LifecycleSummary
						authorName={authorName}
						description={lifecycleDescription}
						modifiedDate={modifiedDate}
						name={lifecycleName}
						onDescriptionChange={onLifecycleDescriptionChange}
						onNameChange={onLifecycleNameChange}
						stageCount={stageConfigs.length}
					/>
				)}
			</BaseEditPage.Main>
		</BaseEditPage.Body>
	</BaseEditPage>
);

export default LifecycleSettingsForm;
