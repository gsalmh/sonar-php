/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.php.checks.phpunit;

import java.util.Optional;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.PhpUnitCheck;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;

import static org.sonar.plugins.php.api.tree.Tree.Kind;

@Rule(key = "S3415")
public class AssertionArgumentOrderCheck extends PhpUnitCheck {

  private static final String MESSAGE = "Swap these 2 arguments so they are in the correct order: expected value, actual value.";
  private static final Kind[] LITERAL = {Kind.BOOLEAN_LITERAL, Kind.NULL_LITERAL, Kind.NUMERIC_LITERAL, Kind.EXPANDABLE_STRING_LITERAL, Kind.REGULAR_STRING_LITERAL};

  public AssertionArgumentOrderCheck() {
    super(true);
  }

  @Override
  public void visitFunctionCall(FunctionCallTree tree) {
    if (!isPhpUnitTestMethod()) {
      return;
    }

    Optional<Assertion> assertion = getAssertion(tree);
    if (tree.arguments().size() >= 2 && assertion.isPresent() && assertion.get().hasExpectedValue()) {
      ExpressionTree expected = tree.arguments().get(0);
      ExpressionTree actual = tree.arguments().get(1);
      if (getAssignedValue(actual).is(LITERAL) && !getAssignedValue(expected).is(LITERAL)) {
        newIssue(actual, MESSAGE).secondary(expected, null);
      }
    }

    super.visitFunctionCall(tree);
  }
}
