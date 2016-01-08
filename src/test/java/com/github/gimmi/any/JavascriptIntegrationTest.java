package com.github.gimmi.any;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class JavascriptIntegrationTest {

   @Test
   public void should_work_from_js() throws ScriptException {
      ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
      assertThat(engine.eval("com.github.gimmi.any.Any.of('str').opt().orElse('');")).isEqualTo("str");
      assertThat(engine.eval("com.github.gimmi.any.Any.NULL.opt().orElse('else');")).isEqualTo("else");
   }
}
