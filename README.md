# Score as a Service
This project is a scoring-as-a-service example. Models are stored in DSE
as PMML.

## Quick Start

### CQL Setup
Execute the CQL found in `src/main/resources/schema.cql`
`cqlsh -f src/main/resources/schema.cql`

The commands issued are:
```
CREATE TABLE IF NOT EXISTS example.model(model_name TEXT, model_version INT, model TEXT, PRIMARY KEY ((model_name), model_version)) WITH CLUSTERING ORDER BY (model_version DESC);
```

### Edit the `application.properties` found in `src/main/resources`.

- set the `dse.localDC`
- set/note the `server.port` (default is `8333`)
- set the `dse.username`
- set the `dse.password`
- set the `dse.keyspace`
- set the path to the credentials file (oh, and place your credentials file in the `resources/` directory)

### Compile with
```
mvn clean package
```

### Run with
```
java -jar target/scoreaas-0.0.1-SNAPSHOT.jar
```

### Go to help screen
Navigate to 
```
http://localhost:8333/model/hello
```

## Endpoints
### Save
Save a model with 
```
http://8333/model/save?model_name=[String]&model_version=[Integer]&model=[String]
```
The model string should be valid PMML.

### Delete
Delete a version of a model with
```
http://8333/model/delete?model_name=[String]&model_version=[Integer]
```

Delete all versions of a model with
```
http://8333/model/deleteAll?model_name=[String]
```

### List
List a version of a model with
```
http://8333/model/listModel?model_name=[String]&model_version=[Integer]
```

List all versions of a model with
```
http://8333/model/listModelVersions?model_name=[String]
```

List all versions of all models with
```
http://8333/model/listAll
```

### Score
Score data with a model
```
http://8333/model/score?model_name=[String]&model_version=[Integer]&input1=[Value]...
```
For each input parameter, list its name and value.  For example:
```
http://localhost:8333/model/score?model_name=iris_mlp&model_version=1&sepal_length=5.1&sepal_width=3.5&petal_length=1.4&petal_width=0.2
```


## Some Examples
### Inserting some data via CQL
```
INSERT INTO scoreaas.model(model_name,model_version,model) 
VALUES ('iris_mlp', 1, '<?xml version="1.0" encoding="UTF-8"?> <PMML version="4.1" xmlns="http://www.dmg.org/PMML-4_1">   <Header copyright="KNIME">     <Application name="KNIME" version="2.8.0"/>   </Header>   <DataDictionary numberOfFields="5">     <DataField dataType="double" name="sepal_length" optype="continuous">       <Interval closure="closedClosed" leftMargin="4.3" rightMargin="7.9"/>     </DataField>     <DataField dataType="double" name="sepal_width" optype="continuous">       <Interval closure="closedClosed" leftMargin="2.0" rightMargin="4.4"/>     </DataField>     <DataField dataType="double" name="petal_length" optype="continuous">       <Interval closure="closedClosed" leftMargin="1.0" rightMargin="6.9"/>     </DataField>     <DataField dataType="double" name="petal_width" optype="continuous">       <Interval closure="closedClosed" leftMargin="0.1" rightMargin="2.5"/>     </DataField>     <DataField dataType="string" name="class" optype="categorical">       <Value value="Iris-setosa"/>       <Value value="Iris-versicolor"/>       <Value value="Iris-virginica"/>     </DataField>   </DataDictionary>   <TransformationDictionary/>   <NeuralNetwork functionName="classification" algorithmName="RProp" activationFunction="logistic" normalizationMethod="none" width="0.0" numberOfLayers="2">     <MiningSchema>       <MiningField name="sepal_length" invalidValueTreatment="asIs"/>       <MiningField name="sepal_width" invalidValueTreatment="asIs"/>       <MiningField name="petal_length" invalidValueTreatment="asIs"/>       <MiningField name="petal_width" invalidValueTreatment="asIs"/>       <MiningField name="class" invalidValueTreatment="asIs" usageType="predicted"/>     </MiningSchema>      <Output>       <OutputField name="predicted_species" feature="predictedValue"/>       <OutputField name="prob_setosa" optype="continuous" dataType="double" feature="probability" value="Iris-setosa"/>       <OutputField name="prob_versicolor" optype="continuous" dataType="double" feature="probability" value="Iris-versicolor"/>       <OutputField name="prob_virginica" optype="continuous" dataType="double" feature="probability" value="Iris-virginica"/>     </Output>          <LocalTransformations>       <DerivedField dataType="double" displayName="sepal_length" name="sepal_length*" optype="continuous">         <Extension extender="KNIME" name="summary" value="Z-Score (Gaussian) normalization on 4 column(s)"/>         <NormContinuous field="sepal_length">           <LinearNorm norm="-7.056602288035726" orig="0.0"/>           <LinearNorm norm="-5.848969266694757" orig="1.0"/>         </NormContinuous>       </DerivedField>       <DerivedField dataType="double" displayName="sepal_width" name="sepal_width*" optype="continuous">         <Extension extender="KNIME" name="summary" value="Z-Score (Gaussian) normalization on 4 column(s)"/>         <NormContinuous field="sepal_width">           <LinearNorm norm="-7.043450340493851" orig="0.0"/>           <LinearNorm norm="-4.737147020096389" orig="1.0"/>         </NormContinuous>       </DerivedField>       <DerivedField dataType="double" displayName="petal_length" name="petal_length*" optype="continuous">         <Extension extender="KNIME" name="summary" value="Z-Score (Gaussian) normalization on 4 column(s)"/>         <NormContinuous field="petal_length">           <LinearNorm norm="-2.130255705592192" orig="0.0"/>           <LinearNorm norm="-1.5634973589465222" orig="1.0"/>         </NormContinuous>       </DerivedField>       <DerivedField dataType="double" displayName="petal_width" name="petal_width*" optype="continuous">         <Extension extender="KNIME" name="summary" value="Z-Score (Gaussian) normalization on 4 column(s)"/>         <NormContinuous field="petal_width">           <LinearNorm norm="-1.5706608073093793" orig="0.0"/>           <LinearNorm norm="-0.26032086795227816" orig="1.0"/>         </NormContinuous>       </DerivedField>     </LocalTransformations>     <NeuralInputs numberOfInputs="4">       <NeuralInput id="0,0">         <DerivedField optype="continuous" dataType="double">           <FieldRef field="sepal_length*"/>         </DerivedField>       </NeuralInput>       <NeuralInput id="0,1">         <DerivedField optype="continuous" dataType="double">           <FieldRef field="sepal_width*"/>         </DerivedField>       </NeuralInput>       <NeuralInput id="0,2">         <DerivedField optype="continuous" dataType="double">           <FieldRef field="petal_length*"/>         </DerivedField>       </NeuralInput>       <NeuralInput id="0,3">         <DerivedField optype="continuous" dataType="double">           <FieldRef field="petal_width*"/>         </DerivedField>       </NeuralInput>     </NeuralInputs>     <NeuralLayer>       <Neuron id="1,0" bias="40.4715596724959">         <Con from="0,0" weight="0.8176653427717075"/>         <Con from="0,1" weight="-9.220948533282769"/>         <Con from="0,2" weight="26.50745889288644"/>         <Con from="0,3" weight="46.892366529773696"/>       </Neuron>       <Neuron id="1,1" bias="42.07393631555714">         <Con from="0,0" weight="0.7673281834576293"/>         <Con from="0,1" weight="-11.442725010790134"/>         <Con from="0,2" weight="27.536429596116776"/>         <Con from="0,3" weight="50.32390234180563"/>       </Neuron>       <Neuron id="1,2" bias="-4.682714809598759">         <Con from="0,0" weight="-0.48068857982178426"/>         <Con from="0,1" weight="-0.6949378788387349"/>         <Con from="0,2" weight="3.5130145878230925"/>         <Con from="0,3" weight="3.374852329493185"/>       </Neuron>     </NeuralLayer>     <NeuralLayer>       <Neuron id="2,0" bias="36.829174221809204">         <Con from="1,0" weight="-15.428606782109018"/>         <Con from="1,1" weight="-58.68586577113855"/>         <Con from="1,2" weight="-4.533681748641222"/>       </Neuron>       <Neuron id="2,1" bias="-3.832065207474468">         <Con from="1,0" weight="4.803555297576479"/>         <Con from="1,1" weight="4.858790438015236"/>         <Con from="1,2" weight="-12.562463287384077"/>       </Neuron>       <Neuron id="2,2" bias="-6.330825024982664">         <Con from="1,0" weight="0.08902632905447753"/>         <Con from="1,1" weight="0.12439444541826992"/>         <Con from="1,2" weight="13.13076076007838"/>       </Neuron>     </NeuralLayer>     <NeuralOutputs numberOfOutputs="3">       <NeuralOutput outputNeuron="2,0">         <DerivedField optype="categorical" dataType="string">           <NormDiscrete field="class" value="Iris-setosa"/>         </DerivedField>       </NeuralOutput>       <NeuralOutput outputNeuron="2,1">         <DerivedField optype="categorical" dataType="string">           <NormDiscrete field="class" value="Iris-versicolor"/>         </DerivedField>       </NeuralOutput>       <NeuralOutput outputNeuron="2,2">         <DerivedField optype="categorical" dataType="string">           <NormDiscrete field="class" value="Iris-virginica"/>         </DerivedField>       </NeuralOutput>     </NeuralOutputs>   </NeuralNetwork> </PMML>');
```

``` 
INSERT INTO scoreaas.model(model_name,model_version,model) 
VALUES ('iris_tree', 1, '<?xml version="1.0" encoding="UTF-8"?>\n<PMML version="4.1" xmlns="http://www.dmg.org/PMML-4_1">\n  <Header copyright="KNIME">\n    <Application name="KNIME" version="2.8.0"/>\n  </Header>\n  <DataDictionary numberOfFields="5">\n    <DataField name="sepal_length" optype="continuous" dataType="double">\n      <Interval closure="closedClosed" leftMargin="4.3" rightMargin="7.9"/>\n    </DataField>\n    <DataField name="sepal_width" optype="continuous" dataType="double">\n      <Interval closure="closedClosed" leftMargin="2.0" rightMargin="4.4"/>\n    </DataField>\n    <DataField name="petal_length" optype="continuous" dataType="double">\n      <Interval closure="closedClosed" leftMargin="1.0" rightMargin="6.9"/>\n    </DataField>\n    <DataField name="petal_width" optype="continuous" dataType="double">\n      <Interval closure="closedClosed" leftMargin="0.1" rightMargin="2.5"/>\n    </DataField>\n    <DataField name="class" optype="categorical" dataType="string">\n      <Value value="Iris-setosa"/>\n      <Value value="Iris-versicolor"/>\n      <Value value="Iris-virginica"/>\n    </DataField>\n  </DataDictionary>\n  <TreeModel modelName="DecisionTree" functionName="classification" splitCharacteristic="binarySplit" missingValueStrategy="lastPrediction" noTrueChildStrategy="returnNullPrediction">\n    <MiningSchema>\n      <MiningField name="sepal_length" invalidValueTreatment="asIs"/>\n      <MiningField name="sepal_width" invalidValueTreatment="asIs"/>\n      <MiningField name="petal_length" invalidValueTreatment="asIs"/>\n      <MiningField name="petal_width" invalidValueTreatment="asIs"/>\n      <MiningField name="class" invalidValueTreatment="asIs" usageType="predicted"/>\n    </MiningSchema>\n\n    <Output>\n      <OutputField name="predicted_species" feature="predictedValue"/>\n      <OutputField name="prob_setosa" optype="continuous" dataType="double" feature="probability" value="Iris-setosa"/>\n      <OutputField name="prob_versicolor" optype="continuous" dataType="double" feature="probability" value="Iris-versicolor"/>\n      <OutputField name="prob_virginica" optype="continuous" dataType="double" feature="probability" value="Iris-virginica"/>\n    </Output>\n\n    <Node id="0" score="Iris-setosa" recordCount="150.0">\n      <True/>\n      <ScoreDistribution value="Iris-setosa" recordCount="50.0"/>\n      <ScoreDistribution value="Iris-versicolor" recordCount="50.0"/>\n      <ScoreDistribution value="Iris-virginica" recordCount="50.0"/>\n      <Node id="1" score="Iris-setosa" recordCount="50.0">\n        <SimplePredicate field="petal_width" operator="lessOrEqual" value="0.6"/>\n        <ScoreDistribution value="Iris-setosa" recordCount="50.0"/>\n        <ScoreDistribution value="Iris-versicolor" recordCount="0.0"/>\n        <ScoreDistribution value="Iris-virginica" recordCount="0.0"/>\n      </Node>\n      <Node id="2" score="Iris-versicolor" recordCount="100.0">\n        <SimplePredicate field="petal_width" operator="greaterThan" value="0.6"/>\n        <ScoreDistribution value="Iris-setosa" recordCount="0.0"/>\n        <ScoreDistribution value="Iris-versicolor" recordCount="50.0"/>\n        <ScoreDistribution value="Iris-virginica" recordCount="50.0"/>\n        <Node id="3" score="Iris-versicolor" recordCount="54.0">\n          <SimplePredicate field="petal_width" operator="lessOrEqual" value="1.7"/>\n          <ScoreDistribution value="Iris-setosa" recordCount="0.0"/>\n          <ScoreDistribution value="Iris-versicolor" recordCount="49.0"/>\n          <ScoreDistribution value="Iris-virginica" recordCount="5.0"/>\n        </Node>\n        <Node id="10" score="Iris-virginica" recordCount="46.0">\n          <SimplePredicate field="petal_width" operator="greaterThan" value="1.7"/>\n          <ScoreDistribution value="Iris-setosa" recordCount="0.0"/>\n          <ScoreDistribution value="Iris-versicolor" recordCount="1.0"/>\n          <ScoreDistribution value="Iris-virginica" recordCount="45.0"/>\n        </Node>\n      </Node>\n    </Node>\n  </TreeModel>\n  <!-- version 2 -->\n</PMML>\n');
```

### Inserting via HTTP
```
http://localhost:8333/model/save?model_name=test_tree&model_version=1&model=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0D%0A%3CPMML+version%3D%224.1%22+xmlns%3D%22http%3A%2F%2Fwww.dmg.org%2FPMML-4_1%22%3E%0D%0A++%3CHeader+copyright%3D%22KNIME%22%3E%0D%0A++++%3CApplication+name%3D%22KNIME%22+version%3D%222.8.0%22%2F%3E%0D%0A++%3C%2FHeader%3E%0D%0A++%3CDataDictionary+numberOfFields%3D%225%22%3E%0D%0A++++%3CDataField+name%3D%22sepal_length%22+optype%3D%22continuous%22+dataType%3D%22double%22%3E%0D%0A++++++%3CInterval+closure%3D%22closedClosed%22+leftMargin%3D%224.3%22+rightMargin%3D%227.9%22%2F%3E%0D%0A++++%3C%2FDataField%3E%0D%0A++++%3CDataField+name%3D%22sepal_width%22+optype%3D%22continuous%22+dataType%3D%22double%22%3E%0D%0A++++++%3CInterval+closure%3D%22closedClosed%22+leftMargin%3D%222.0%22+rightMargin%3D%224.4%22%2F%3E%0D%0A++++%3C%2FDataField%3E%0D%0A++++%3CDataField+name%3D%22petal_length%22+optype%3D%22continuous%22+dataType%3D%22double%22%3E%0D%0A++++++%3CInterval+closure%3D%22closedClosed%22+leftMargin%3D%221.0%22+rightMargin%3D%226.9%22%2F%3E%0D%0A++++%3C%2FDataField%3E%0D%0A++++%3CDataField+name%3D%22petal_width%22+optype%3D%22continuous%22+dataType%3D%22double%22%3E%0D%0A++++++%3CInterval+closure%3D%22closedClosed%22+leftMargin%3D%220.1%22+rightMargin%3D%222.5%22%2F%3E%0D%0A++++%3C%2FDataField%3E%0D%0A++++%3CDataField+name%3D%22class%22+optype%3D%22categorical%22+dataType%3D%22string%22%3E%0D%0A++++++%3CValue+value%3D%22Iris-setosa%22%2F%3E%0D%0A++++++%3CValue+value%3D%22Iris-versicolor%22%2F%3E%0D%0A++++++%3CValue+value%3D%22Iris-virginica%22%2F%3E%0D%0A++++%3C%2FDataField%3E%0D%0A++%3C%2FDataDictionary%3E%0D%0A++%3CTreeModel+modelName%3D%22DecisionTree%22+functionName%3D%22classification%22+splitCharacteristic%3D%22binarySplit%22+missingValueStrategy%3D%22lastPrediction%22+noTrueChildStrategy%3D%22returnNullPrediction%22%3E%0D%0A++++%3CMiningSchema%3E%0D%0A++++++%3CMiningField+name%3D%22sepal_length%22+invalidValueTreatment%3D%22asIs%22%2F%3E%0D%0A++++++%3CMiningField+name%3D%22sepal_width%22+invalidValueTreatment%3D%22asIs%22%2F%3E%0D%0A++++++%3CMiningField+name%3D%22petal_length%22+invalidValueTreatment%3D%22asIs%22%2F%3E%0D%0A++++++%3CMiningField+name%3D%22petal_width%22+invalidValueTreatment%3D%22asIs%22%2F%3E%0D%0A++++++%3CMiningField+name%3D%22class%22+invalidValueTreatment%3D%22asIs%22+usageType%3D%22predicted%22%2F%3E%0D%0A++++%3C%2FMiningSchema%3E%0D%0A%0D%0A++++%3COutput%3E%0D%0A++++++%3COutputField+name%3D%22predicted_species%22+feature%3D%22predictedValue%22%2F%3E%0D%0A++++++%3COutputField+name%3D%22prob_setosa%22+optype%3D%22continuous%22+dataType%3D%22double%22+feature%3D%22probability%22+value%3D%22Iris-setosa%22%2F%3E%0D%0A++++++%3COutputField+name%3D%22prob_versicolor%22+optype%3D%22continuous%22+dataType%3D%22double%22+feature%3D%22probability%22+value%3D%22Iris-versicolor%22%2F%3E%0D%0A++++++%3COutputField+name%3D%22prob_virginica%22+optype%3D%22continuous%22+dataType%3D%22double%22+feature%3D%22probability%22+value%3D%22Iris-virginica%22%2F%3E%0D%0A++++%3C%2FOutput%3E%0D%0A%0D%0A++++%3CNode+id%3D%220%22+score%3D%22Iris-setosa%22+recordCount%3D%22150.0%22%3E%0D%0A++++++%3CTrue%2F%3E%0D%0A++++++%3CScoreDistribution+value%3D%22Iris-setosa%22+recordCount%3D%2250.0%22%2F%3E%0D%0A++++++%3CScoreDistribution+value%3D%22Iris-versicolor%22+recordCount%3D%2250.0%22%2F%3E%0D%0A++++++%3CScoreDistribution+value%3D%22Iris-virginica%22+recordCount%3D%2250.0%22%2F%3E%0D%0A++++++%3CNode+id%3D%221%22+score%3D%22Iris-setosa%22+recordCount%3D%2250.0%22%3E%0D%0A++++++++%3CSimplePredicate+field%3D%22petal_width%22+operator%3D%22lessOrEqual%22+value%3D%220.6%22%2F%3E%0D%0A++++++++%3CScoreDistribution+value%3D%22Iris-setosa%22+recordCount%3D%2250.0%22%2F%3E%0D%0A++++++++%3CScoreDistribution+value%3D%22Iris-versicolor%22+recordCount%3D%220.0%22%2F%3E%0D%0A++++++++%3CScoreDistribution+value%3D%22Iris-virginica%22+recordCount%3D%220.0%22%2F%3E%0D%0A++++++%3C%2FNode%3E%0D%0A++++++%3CNode+id%3D%222%22+score%3D%22Iris-versicolor%22+recordCount%3D%22100.0%22%3E%0D%0A++++++++%3CSimplePredicate+field%3D%22petal_width%22+operator%3D%22greaterThan%22+value%3D%220.6%22%2F%3E%0D%0A++++++++%3CScoreDistribution+value%3D%22Iris-setosa%22+recordCount%3D%220.0%22%2F%3E%0D%0A++++++++%3CScoreDistribution+value%3D%22Iris-versicolor%22+recordCount%3D%2250.0%22%2F%3E%0D%0A++++++++%3CScoreDistribution+value%3D%22Iris-virginica%22+recordCount%3D%2250.0%22%2F%3E%0D%0A++++++++%3CNode+id%3D%223%22+score%3D%22Iris-versicolor%22+recordCount%3D%2254.0%22%3E%0D%0A++++++++++%3CSimplePredicate+field%3D%22petal_width%22+operator%3D%22lessOrEqual%22+value%3D%221.7%22%2F%3E%0D%0A++++++++++%3CScoreDistribution+value%3D%22Iris-setosa%22+recordCount%3D%220.0%22%2F%3E%0D%0A++++++++++%3CScoreDistribution+value%3D%22Iris-versicolor%22+recordCount%3D%2249.0%22%2F%3E%0D%0A++++++++++%3CScoreDistribution+value%3D%22Iris-virginica%22+recordCount%3D%225.0%22%2F%3E%0D%0A++++++++%3C%2FNode%3E%0D%0A++++++++%3CNode+id%3D%2210%22+score%3D%22Iris-virginica%22+recordCount%3D%2246.0%22%3E%0D%0A++++++++++%3CSimplePredicate+field%3D%22petal_width%22+operator%3D%22greaterThan%22+value%3D%221.7%22%2F%3E%0D%0A++++++++++%3CScoreDistribution+value%3D%22Iris-setosa%22+recordCount%3D%220.0%22%2F%3E%0D%0A++++++++++%3CScoreDistribution+value%3D%22Iris-versicolor%22+recordCount%3D%221.0%22%2F%3E%0D%0A++++++++++%3CScoreDistribution+value%3D%22Iris-virginica%22+recordCount%3D%2245.0%22%2F%3E%0D%0A++++++++%3C%2FNode%3E%0D%0A++++++%3C%2FNode%3E%0D%0A++++%3C%2FNode%3E%0D%0A++%3C%2FTreeModel%3E%0D%0A%3C%2FPMML%3E
```

### Scoring
```
curl "http://localhost:8333/model/score?model_name=iris_mlp&model_version=1&sepal_length=5.1&sepal_width=3.5&petal_length=1.4&petal_width=0.2"
```

or just
```
http://localhost:8333/model/score?model_name=iris_mlp&model_version=1&sepal_length=5.1&sepal_width=3.5&petal_length=1.4&petal_width=0.2
```
