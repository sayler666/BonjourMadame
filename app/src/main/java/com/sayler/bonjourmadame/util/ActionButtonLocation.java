package com.sayler.bonjourmadame.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActionButtonLocation {
  private Map<Integer, Integer> rulesToAdd;
  private Map<Integer, Integer> rulesToRemove;

  public Map<Integer, Integer> getRulesToAdd() {
    return Collections.unmodifiableMap(rulesToAdd);
  }

  public Map<Integer, Integer> getRulesToRemove() {
    return Collections.unmodifiableMap(rulesToRemove);
  }

  private ActionButtonLocation(ActionButtonLocationBuilder actionButtonLocationBuilder) {
    this.rulesToAdd = actionButtonLocationBuilder.rulesToAdd;
    this.rulesToRemove = actionButtonLocationBuilder.rulesToRemove;
  }

  public static class ActionButtonLocationBuilder {
    private Map<Integer, Integer> rulesToAdd;
    private Map<Integer, Integer> rulesToRemove;

    public ActionButtonLocationBuilder() {
      rulesToAdd = new HashMap<>();
      rulesToRemove = new HashMap<>();
    }

    public ActionButtonLocationBuilder addRule(Integer rule, Integer anchor) {
      anchor = anchor == null ? -1 : anchor;
      rulesToAdd.put(rule, anchor);
      return ActionButtonLocationBuilder.this;
    }

    public ActionButtonLocationBuilder removeRule(Integer rule) {
      rulesToRemove.put(rule, -1);
      return ActionButtonLocationBuilder.this;
    }

    public ActionButtonLocation build() {
      return new ActionButtonLocation(this);
    }
  }
}