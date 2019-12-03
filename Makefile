buildjson:
	@ruby -rjson -ryaml -e "puts YAML.load_file('src/main/resources/data.yaml').to_json" > src/main/resources/data.json
