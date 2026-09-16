package br.org.maosemacao.bdd;
import org.junit.platform.suite.api.*;
@Suite @IncludeEngines("cucumber") @SelectClasspathResource("features")
@ConfigurationParameter(key="cucumber.glue",value="br.org.maosemacao.bdd")
@ConfigurationParameter(key="cucumber.plugin",value="pretty,html:target/cucumber.html,json:target/cucumber.json")
public class RunCucumberTest { }
