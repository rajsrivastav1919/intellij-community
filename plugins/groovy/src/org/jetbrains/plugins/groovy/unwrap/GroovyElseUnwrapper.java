/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.groovy.unwrap;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrIfStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;

import java.util.List;

public class GroovyElseUnwrapper extends GroovyElseUnwrapperBase {
  public GroovyElseUnwrapper() {
    super(CodeInsightBundle.message("unwrap.else"));
  }

  @Override
  public PsiElement collectAffectedElements(PsiElement e, List<PsiElement> toExtract) {
    super.collectAffectedElements(e, toExtract);
    return findTopmostIfStatement(e);
  }

  @Override
  protected void unwrapElseBranch(GrStatement branch, PsiElement parent, Context context) throws IncorrectOperationException {
    // if we have 'else if' then we have to extract statements from the 'if' branch
    if (branch instanceof GrIfStatement) {
      branch = ((GrIfStatement)branch).getThenBranch();
    }

    parent = findTopmostIfStatement(parent);

    context.extractFromBlockOrSingleStatement(branch, parent);
    context.delete(parent);
  }

  private static PsiElement findTopmostIfStatement(PsiElement parent) {
    while (parent.getParent() instanceof GrIfStatement) {
      parent = parent.getParent();
    }
    return parent;
  }
}