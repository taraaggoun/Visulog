package up.visulog.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * dispose d’un chemin d’accès non mutable vers le dépôt git, ainsi que d’un dictionnaire qui associe à un nom de plugin sa configuration de forme PluginConfig.
 */
public class Configuration {

    //Attributs
    private final Path gitPath; // type Path
    private final Map<String, PluginConfig> plugins; // une map qui associe une chaine de caractere a un PluginConfig

    //Constructeur
    /**
     * Construit un objet de type Configuration
     * @param gitPath
     * @param plugins
     */
    public Configuration(Path gitPath, Map<String, PluginConfig> plugins) {
        this.gitPath = gitPath;
        this.plugins = Map.copyOf(plugins);
    }

    //Getteurs
    /**
     * Retourne gitPath
     * @return
     */
    public Path getGitPath() {
        return gitPath;
    }
    /**
     * Retourne plugins
     * @return
     */
    public Map<String, PluginConfig> getPluginConfigs() {
        return plugins;
    }
}
