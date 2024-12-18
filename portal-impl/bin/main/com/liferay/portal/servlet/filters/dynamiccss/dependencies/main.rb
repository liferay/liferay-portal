require 'compass'
require 'sass/plugin'

class SASSWrapper
	def initialize()
		Compass.add_project_configuration

		@load_paths = Compass.configuration.sass_load_paths
	end

	def process(content, commonSassPath, cssRealPath, cssThemePath, sassCachePath, debug=false)
		Compass.configuration.project_path ||= cssThemePath

		load_paths = [commonSassPath, cssThemePath]
		load_paths += @load_paths

		engine = Sass::Engine.new(
			content,
			{
				:cache_location => sassCachePath,
				:debug_info => debug,
				:filename => cssRealPath,
				:full_exception => debug,
				:line => 0,
				:load_paths => load_paths,
				:syntax => :scss,
				:ugly => true
			}
		)

		engine.render
	end
end

SASSWrapper.new()