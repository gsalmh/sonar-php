/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.php.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.php.api.PHPPunctuator;
import org.sonar.php.parser.PHPGrammar;

@Rule(
  key = "S131",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class SwitchWithoutDefaultCheck extends SquidCheck<Grammar> {

  @Override
  public void init() {
    subscribeTo(PHPGrammar.CASE_LIST);
  }

  @Override
  public void visitNode(AstNode astNode) {
    AstNode defaultClause = astNode.getFirstChild(PHPGrammar.DEFAULT_CLAUSE);

    if (defaultClause == null) {
      getContext().createLineViolation(this, "Add a \"case default\" clause to this \"switch\" statement.", astNode.getParent());
    } else if (defaultClause.getNextAstNode().isNot(PHPPunctuator.RCURLYBRACE)) {
      getContext().createLineViolation(this, "Move this \"case default\" clause to the end of this \"switch\" statement.", defaultClause);
    }
  }
}