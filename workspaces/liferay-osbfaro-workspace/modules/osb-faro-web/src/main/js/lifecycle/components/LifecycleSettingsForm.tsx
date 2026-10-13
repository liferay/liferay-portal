import BaseEditPage from 'shared/components/base-edit-page';
import LifecycleConditionsPanel from 'lifecycle/components/LifecycleConditionsPanel';
import React from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import {ICatalogField} from 'shared/api/catalog';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';

interface ILifecycleSettingsFormProps {
	backURL: string;
	catalogFields?: ICatalogField[];
	lifecycleName: string;
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
	backURL,
	catalogFields,
	lifecycleName,
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
						defaultExpanded
						fields={catalogFields}
						index={stageConfigs.indexOf(selectedStage) + 1}
						key={selectedStage.key}
						name={selectedStage.name}
						onChange={(value) =>
							onStageChange(selectedStage.key, value)
						}
						value={selectedStage}
					/>
				) : (
					<BaseEditPage.Title
						id="lifecycleName"
						label={Liferay.Language.get('title')}
						onChange={(event) =>
							onLifecycleNameChange(event.target.value)
						}
						placeholder={Liferay.Language.get('lifecycle-name')}
						required
						value={lifecycleName}
					/>
				)}
			</BaseEditPage.Main>
		</BaseEditPage.Body>
	</BaseEditPage>
);

export default LifecycleSettingsForm;
