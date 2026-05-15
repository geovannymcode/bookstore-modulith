/**
 * Módulo Orders — gestión del ciclo de vida de las órdenes.
 *
 * allowedDependencies declara con exactitud de qué módulos puede
 * depender Orders. Si alguien agrega una dependencia a 'inventory'
 * desde aquí, ModularityTest falla con un mensaje claro.
 */
@ApplicationModule(allowedDependencies = {"catalog", "common"})
package com.geovannycode.bookstore.orders;

import org.springframework.modulith.ApplicationModule;
