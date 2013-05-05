package com.benromberg.packagecycles;

import jdepend.framework.JDepend;
import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NoPackageCyclesRule implements EnforcerRule {

    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        try {
            executePackageCycleCheckIfNecessary(helper);
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression "
                    + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new EnforcerRuleException("Unable to access target directory "
                    + e.getLocalizedMessage(), e);
        }
    }

    private void executePackageCycleCheckIfNecessary(EnforcerRuleHelper helper) throws ExpressionEvaluationException, IOException, EnforcerRuleException {
        MavenProject project = (MavenProject) helper.evaluate("${project}");
        File targetDir = new File((String) helper.evaluate("${project.build.directory}"));
        File classesDir = new File(targetDir, "classes");

        if (checkIsNecessary(project, classesDir)) {
            executePackageCycleCheck(classesDir);
        }
    }

    private void executePackageCycleCheck(File classesDir) throws IOException, EnforcerRuleException {
        JDepend jdepend = new JDepend();
        jdepend.addDirectory(classesDir.getAbsolutePath());
        jdepend.analyze();
        if (jdepend.containsCycles()) {
            throw new EnforcerRuleException("There are package cycles:" + getPackageCycles(jdepend));
        }
    }

    private String getPackageCycles(JDepend jdepend) {
        StringBuilder cycles = new StringBuilder();
        for (JavaPackage pack : (Collection<JavaPackage>) jdepend.getPackages()) {
            if (pack.containsCycle()) {
                collectCycles(cycles, pack);
            }
        }
        return cycles.toString();
    }

    private void collectCycles(StringBuilder message, JavaPackage pack) {
        message.append("\n").append(pack.getName()).append(" has cyclic dependency to: ");
        List<JavaPackage> cycleList = new ArrayList<JavaPackage>();
        pack.collectAllCycles(cycleList);
        removeOwnPackageAndDuplicates(pack, cycleList);
        appendCycles(pack, message, cycleList);
    }

    private void removeOwnPackageAndDuplicates(JavaPackage pack, List<JavaPackage> cycleList) {
        Set<JavaPackage> cycleListWithoutDuplicates = new HashSet<JavaPackage>(cycleList);
        cycleList.clear();
        cycleList.addAll(cycleListWithoutDuplicates);
        cycleList.remove(pack);
    }

    private void appendCycles(JavaPackage pack, StringBuilder message, List<JavaPackage> cycleList) {
        boolean first = true;
        for (JavaPackage cyclePackage : cycleList) {
            if (!first) {
                message.append(", ");
            }
            appendCycleWithClasses(pack, message, cyclePackage);
            first = false;
        }
    }

    private void appendCycleWithClasses(JavaPackage pack, StringBuilder message, JavaPackage cyclePackage) {
        message.append(cyclePackage.getName()).append(" (");
        appendClasses(pack, message, cyclePackage);
        message.append(")");
    }

    private void appendClasses(JavaPackage pack, StringBuilder message, JavaPackage cyclePackage) {
        boolean first = true;
        for (JavaClass clazz : (Collection<JavaClass>) cyclePackage.getClasses()) {
            if (clazz.getImportedPackages().contains(pack)) {
                if (!first) {
                    message.append(", ");
                }
                message.append(clazz.getName().substring(clazz.getPackageName().length() + 1));
                first = false;
            }
        }
    }

    private boolean checkIsNecessary(MavenProject project, File classesDir) {
        return project.getPackaging().equalsIgnoreCase("jar") && classesDir.exists();
    }

    public String getCacheId() {
        return "";
    }

    public boolean isCacheable() {
        return false;
    }

    public boolean isResultValid(EnforcerRule arg0) {
        return false;
    }
}