package com.github.michaelengland.fliptheswitch.gradle;

import com.github.michaelengland.fliptheswitch.Feature;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

public class FeaturesWriter {
    private List<Feature> defaultFeatures;

    public FeaturesWriter(final List<Feature> defaultFeatures) {
        this.defaultFeatures = defaultFeatures;
    }

    public JavaFile build() {
        return JavaFile.builder(flipTheSwitchPackageName(), type())
                .build();
    }

    private TypeSpec type() {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(featuresTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(flipTheSwitchTypeName(), "flipTheSwitch", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(constructor())
                .addMethod(withMethod())
                .addMethod(getDefaultFeaturesMethod());
        for (Feature feature : defaultFeatures) {
            typeBuilder.addMethod(isFeatureEnabledMethod(feature))
                    .addMethod(enableFeatureMethod(feature))
                    .addMethod(disableFeatureMethod(feature))
                    .addMethod(setFeatureEnabledMethod(feature))
                    .addMethod(resetFeatureMethod(feature));
        }
        return typeBuilder
                .addMethod(resetAllFeaturesMethod())
                .build();
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addParameter(flipTheSwitchTypeName(), "flipTheSwitch", Modifier.FINAL)
                .addStatement("this.$N = $N", "flipTheSwitch", "flipTheSwitch")
                .build();
    }

    private MethodSpec withMethod() {
        return MethodSpec.methodBuilder("with")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(featuresTypeName())
                .addParameter(contextTypeName(), "context", Modifier.FINAL)
                .addStatement("return new $T($T.with($N, $N()))", featuresTypeName(),
                        flipTheSwitchTypeName(), "context", getDefaultFeaturesMethod())
                .build();
    }

    private MethodSpec getDefaultFeaturesMethod() {
        MethodSpec.Builder defaultFeaturesMethodBuilder = MethodSpec.methodBuilder("getDefaultFeatures")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(listOfFeaturesTypeName())
                .addStatement("$T defaultFeatures = new $T<>()", listOfFeaturesTypeName(), ArrayList.class);
        for (Feature feature : defaultFeatures) {
            defaultFeaturesMethodBuilder.addStatement("defaultFeatures.add(new $T($S, $S, $L))", Feature.class,
                    feature.getName(), feature.getDescription(), feature.isEnabled());
        }
        return defaultFeaturesMethodBuilder.addStatement("return defaultFeatures")
                .build();
    }

    private MethodSpec isFeatureEnabledMethod(final Feature feature) {
        return MethodSpec.methodBuilder("is" + camelCaseFeatureName(feature) + "Enabled")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return $N.isFeatureEnabled($S)", "flipTheSwitch", feature.getName())
                .build();
    }

    private MethodSpec enableFeatureMethod(final Feature feature) {
        return MethodSpec.methodBuilder("enable" + camelCaseFeatureName(feature))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.enableFeature($S)", "flipTheSwitch", feature.getName())
                .build();
    }

    private MethodSpec disableFeatureMethod(final Feature feature) {
        return MethodSpec.methodBuilder("disable" + camelCaseFeatureName(feature))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.disableFeature($S)", "flipTheSwitch", feature.getName())
                .build();
    }

    private MethodSpec setFeatureEnabledMethod(final Feature feature) {
        return MethodSpec.methodBuilder("set" + camelCaseFeatureName(feature) + "Enabled")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(boolean.class, "enabled", Modifier.FINAL)
                .addStatement("$N.setFeatureEnabled($S, $N)", "flipTheSwitch", feature.getName(), "enabled")
                .build();
    }

    private MethodSpec resetFeatureMethod(final Feature feature) {
        return MethodSpec.methodBuilder("reset" + camelCaseFeatureName(feature))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.resetFeature($S)", "flipTheSwitch", feature.getName())
                .build();
    }

    private String camelCaseFeatureName(final Feature feature) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, feature.getName());
    }

    private MethodSpec resetAllFeaturesMethod() {
        return MethodSpec.methodBuilder("resetAllFeatures")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N.resetAllFeatures()", "flipTheSwitch")
                .build();
    }

    private ClassName flipTheSwitchTypeName() {
        return ClassName.get(flipTheSwitchPackageName(), flipTheSwitchClassName());
    }

    private ClassName featuresTypeName() {
        return ClassName.get(flipTheSwitchPackageName(), featuresClassName());
    }

    private ClassName contextTypeName() {
        return ClassName.get(androidContentPackageName(), contextClassName());
    }

    private ParameterizedTypeName listOfFeaturesTypeName() {
        return ParameterizedTypeName.get(List.class, Feature.class);
    }

    private String flipTheSwitchPackageName() {
        return "com.github.michaelengland.fliptheswitch";
    }

    private String androidContentPackageName() {
        return "android.content";
    }

    private String featuresClassName() {
        return "Features";
    }

    private String flipTheSwitchClassName() {
        return "FlipTheSwitch";
    }

    private String contextClassName() {
        return "Context";
    }
}