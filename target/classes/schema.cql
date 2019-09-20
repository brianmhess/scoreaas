CREATE KEYSPACE IF NOT EXISTS scoreaas WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
CREATE TABLE IF NOT EXISTS scoreaas.model(model_name TEXT, model_version INT, model TEXT, PRIMARY KEY ((model_name), model_version)) WITH CLUSTERING ORDER BY (model_version DESC);
