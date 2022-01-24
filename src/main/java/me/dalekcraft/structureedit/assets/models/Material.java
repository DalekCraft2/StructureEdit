package me.dalekcraft.structureedit.assets.models;

import me.dalekcraft.structureedit.assets.ResourceLocation;

import java.util.Objects;

public class Material {

    private final ResourceLocation texture;

    public Material(ResourceLocation texture) {
        this.texture = texture;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Material material = (Material) obj;
        return texture.equals(material.texture);
    }

    public int hashCode() {
        return Objects.hash(texture);
    }

    public String toString() {
        return "Material{texture=" + texture + "}";
    }
}
