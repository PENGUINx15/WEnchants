package me.penguinx13.wenchants;

import me.penguinx13.wapi.commands.core.pipeline.*;
import me.penguinx13.wapi.commands.core.registry.CommandRegistrationService;
import me.penguinx13.wapi.commands.core.resolver.DefaultResolvers;
import me.penguinx13.wapi.commands.core.resolver.ResolverRegistry;
import me.penguinx13.wapi.commands.core.runtime.CommandRuntime;
import me.penguinx13.wapi.commands.core.runtime.NoopMetricsSink;
import me.penguinx13.wapi.commands.core.validation.ValidationService;
import me.penguinx13.wapi.commands.paper.error.DefaultErrorPresenter;
import me.penguinx13.wapi.commands.paper.platform.*;
import me.penguinx13.wapi.enchants.api.EnchantRegistry;
import me.penguinx13.wapi.enchants.manager.EnchantManager;
import me.penguinx13.wapi.enchants.storage.EnchantStorage;
import me.penguinx13.wenchants.commands.Commands;
import me.penguinx13.wenchants.enchants.BoerEnchant;
import me.penguinx13.wenchants.enchants.EnchantListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public final class WEnchants extends JavaPlugin {

    @Override
    public void onEnable() {
        EnchantRegistry enchantRegistry = new EnchantRegistry();
        BoerEnchant boerEnchant = new BoerEnchant();

        enchantRegistry.register(boerEnchant);

        EnchantStorage enchantStorage = new EnchantStorage(this, enchantRegistry);
        EnchantManager enchantManager = new EnchantManager(enchantStorage);

        CommandRegistrationService registrationService = new CommandRegistrationService();
        registrationService.register(
                new Commands(enchantStorage, boerEnchant));

        ResolverRegistry resolverRegistry = new ResolverRegistry();
        DefaultResolvers.registerDefaults(resolverRegistry);
        resolverRegistry.register(new PaperPlayerResolver());

        ValidationService validationService = new ValidationService();
        PaperPlatformBridge bridge = new PaperPlatformBridge(
                new PaperScheduler(this));

        CommandRuntime runtime = new CommandRuntime(
                registrationService.buildTree(),
                new CommandPipeline(List.of(
                        new RoutingStage(),
                        new ArgumentParsingStage(),
                        new ValidationStage(),
                        new AuthorizationStage(),
                        new InvocationStage(),
                        new PostProcessingStage()
                )),
                resolverRegistry,
                validationService,
                new DefaultErrorPresenter(new PaperLogger(getLogger())),
                List.of(),
                bridge,
                new NoopMetricsSink()
        );

        new PaperCommandBinder(this, bridge).bind(runtime);
        getServer().getPluginManager().registerEvents(
                new EnchantListener(enchantManager), this
        );
    }

}
