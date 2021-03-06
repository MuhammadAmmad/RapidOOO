package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreTypes;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOIgnore;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.util.GlobalEnvironment;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FromEntry {
    private OOOs ooosAnno;

    private String suffix;
    private String fromSuffix;
    private List<String> fromPackages;

    private Element generatorClassEl;
    /**
     * key: pojo class name
     */
    private Map<String, TargetElement> allFromElements = new LinkedHashMap<>();

    /**
     * key: @OOO.id
     */
    private Map<String, TargetElement> allFromElementIds = new HashMap<>();

    public void setOoosAnno(OOOs ooosAnno) {
        this.ooosAnno = ooosAnno;
    }

    public void parse() {
        suffix = ooosAnno.suffix();
        fromSuffix = ooosAnno.fromSuffix();
        fromPackages = Arrays.asList(ooosAnno.ooosPackages());

        for (String ooosPackage : fromPackages) {
            PackageElement packageElement = GlobalEnvironment.getElementUtils().getPackageElement(ooosPackage);
            if (null == packageElement) {
                throw new RuntimeException("package[" + ooosPackage + "] is not exist.");
//                continue;
            }

            List<? extends Element> oooClassesElements = packageElement.getEnclosedElements();
            if (null != oooClassesElements) {
                for (Element oooClassElement : oooClassesElements) {
                    if (null != oooClassElement.getAnnotation(OOOs.class)) {
                        continue;
                    }

                    if (null != oooClassElement.getAnnotation(OOOIgnore.class)) {
                        LogUtil.logger("Ignore `From Class` [" + oooClassElement.toString() + "](@OOOIgnore).");
                        continue;
                    }

                    TargetElement targetElement = generateBaseFromElement(oooClassElement);
                    allFromElements.put(MoreTypes.asTypeElement(oooClassElement.asType()).getQualifiedName().toString(), targetElement);

                }
            }
        }

        // special ooos
        OOO[] ooos = ooosAnno.ooos();
        for (OOO ooo : ooos) {
            TypeMirror fromTypeMirror = getFromTypeMirror(ooo);
            if (null == fromTypeMirror) {
                continue;
            }
            if (null != MoreTypes.asTypeElement(fromTypeMirror).getAnnotation(OOOIgnore.class)) {
                LogUtil.logger("Ignore `From Class` [" + fromTypeMirror.toString() + "](@OOOIgnore).");
                continue;
            }
            String specialQualifiedName = MoreTypes.asTypeElement(fromTypeMirror).getQualifiedName().toString();
            TargetElement targetElement = allFromElements.get(specialQualifiedName);
            if (null == targetElement) {
                targetElement = generateBaseFromElement(MoreTypes.asElement(fromTypeMirror));
                allFromElements.put(specialQualifiedName, targetElement);
            }

            // cache from element ids
            allFromElementIds.put(ooo.id(), targetElement);

            targetElement.setOooAnno(ooo);
        }

        for(Map.Entry<String, TargetElement> fromElement : allFromElements.entrySet()){
            fromElement.getValue().parseBase();
        }

        for(Map.Entry<String, TargetElement> fromElement : allFromElements.entrySet()){
            fromElement.getValue().parse();
        }

    }

    private TargetElement generateBaseFromElement(Element oooClassElement) {
        TargetElement targetElement = new TargetElement();
        targetElement.setFromEntry(this);
        targetElement.setFromElement(oooClassElement);
        targetElement.setGeneratorClassEl(generatorClassEl);
        targetElement.setFromSuffix(fromSuffix);
        targetElement.setSuffix(suffix);
        return targetElement;
    }

    public OOOs getOoosAnno() {
        return ooosAnno;
    }

    public String getSuffix() {
        return suffix;
    }


    public String getFromSuffix() {
        return fromSuffix;
    }


    public List<String> getFromPackages() {
        return fromPackages;
    }


    private static TypeMirror getFromTypeMirror(OOO ooo) {
        try {
            ooo.from();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null;
    }


    public Map<String, TargetElement> getAllFromElements() {
        return allFromElements;
    }

    public Element getGeneratorClassEl() {
        return generatorClassEl;
    }

    public void setGeneratorClassEl(Element generatorClassEl) {
        this.generatorClassEl = generatorClassEl;
    }

    public TargetElement getFromElementById(String id) {
        return allFromElementIds.get(id);
    }

    public Map<String, TargetElement> getAllFromElementIds() {
        return allFromElementIds;
    }

    @Override
    public String toString() {
        return "FromEntry{" +
                "ooosAnno=" + ooosAnno +
                ", suffix='" + suffix + '\'' +
                ", fromSuffix='" + fromSuffix + '\'' +
                ", fromPackages=" + fromPackages +
                ", allFromElements=" + allFromElements +
                '}';
    }
}
