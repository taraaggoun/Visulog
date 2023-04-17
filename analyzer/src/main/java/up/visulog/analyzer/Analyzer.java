package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.lang.reflect.InvocationTargetException;

/**
 * La classe Analyser qui s’assimile à une liste de plugins à lancer. 
 */
public class Analyzer{
    
    //Attributs
    private final Configuration config; // type Configuration
    private AnalyzerResult result; // type AnalyzerResult

    //Constructeur
    /**
     * //Construit un objet de type Analyzer
     * @param config 
     */
    public Analyzer(Configuration config) {
        this.config = config;
    }
    
    //Methodes
    /**
     *retourne un AnalyzerResult fait d'une liste AnalyzerPlugin en fonction de la configuration config
     *@return 
     */
    public AnalyzerResult computeResults() {
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            var pluginName = pluginConfigEntry.getKey();
            var pluginConfig = pluginConfigEntry.getValue();
            var plugin = makePlugin(pluginName, pluginConfig);
            plugin.ifPresent(plugins::add);
        }
        // lance les plugins
        for (var plugin: plugins) plugin.run();
        // lance les plugins en parallèle
        for (var plugin: plugins){
        	Thread t = new Thread(plugin);
        	t.start();
        }
        // retourne les résultat deans une instance AnalyzerResult
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    /**
     * retoune un Optional de AnalyzerPlugin, prend en paramètre un nom de plugin et ses configuration et instancie le plugin
     * @param pluginName
     * @param PluginConfig
     * @return
     * @exception e
     */
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
        try {
            return Optional.of(instanciateur(pluginName));
        }
        catch (Exception e){
            return Optional.empty();
        }
    }

    /**
     * Retourne un AnalyzerPlugin, et prend un nom en parametre et instancie le plugin
     * Récupère le constructeur de la classe nommée nom qui prend en arg une config.
     * @param nom
     * @return 
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public AnalyzerPlugin instanciateur(String nom) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (AnalyzerPlugin) Class.forName("up.visulog.analyzer." + nom).getConstructor(config.getClass()).newInstance(config);
    }
}
