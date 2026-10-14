import BaseEditPage from 'shared/components/base-edit-page';
import Card from 'shared/components/Card';
import React from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import {ICatalogField} from 'shared/api/catalog';
import {
	IStageConfig,
	LIFECYCLE_STAGE_ORDER,
} from 'lifecycle/utils/stageConfiguration';

interface ILifecycleSettingsFormProps {
	backURL: string;
	catalogFields?: ICatalogField[];
	lifecycleName: string;
	onLifecycleNameChange: (name: string) => void;
	onStageChange: (index: number, value: IStageConfig) => void;
	onSubmit: () => void;
	stageConfigs: IStageConfig[];
	submitDisabled?: boolean;
	submitLabel: string;
}

const LifecycleSettingsForm: React.FC<ILifecycleSettingsFormProps> = ({
	backURL,
	catalogFields,
	lifecycleName,
	onLifecycleNameChange,
	onStageChange,
	onSubmit,
	stageConfigs,
	submitDisabled = false,
	submitLabel,
}) => (
	<BaseEditPage
		className="d-flex flex-column"
		documentTitle={Liferay.Language.get('lifecycle-settings')}
	>
		<BaseEditPage.Toolbar
			backURL={backURL}
			title={Liferay.Language.get('lifecycle-settings')}
		>
			<BaseEditPage.Toolbar.Cancel href={backURL} />

			<BaseEditPage.Toolbar.Save
				disabled={submitDisabled}
				label={submitLabel}
				onClick={onSubmit}
			/>
		</BaseEditPage.Toolbar>

		<BaseEditPage.Body>
			<BaseEditPage.Main>
				<div className="mb-4">
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
				</div>

				<Card>
					<Card.Body>
						<Card.Title>
							{Liferay.Language.get('stage-configuration')}
						</Card.Title>

						<p className="mt-3 text-secondary">
							{Liferay.Language.get(
								'define-entry-conditions-for-each-lifecycle-stage-an-account-moves-to-a-stage-when-it-meets-the-selected-conditions'
							)}
						</p>

						{LIFECYCLE_STAGE_ORDER.map((stageType, index) => (
							<StageConfigurationPanel
								defaultExpanded={index === 0}
								fields={catalogFields}
								index={index + 1}
								key={stageType}
								onChange={(value) =>
									onStageChange(index, value)
								}
								stageType={stageType}
								value={stageConfigs[index]}
							/>
						))}
					</Card.Body>
				</Card>
			</BaseEditPage.Main>
		</BaseEditPage.Body>
	</BaseEditPage>
);

export default LifecycleSettingsForm;
