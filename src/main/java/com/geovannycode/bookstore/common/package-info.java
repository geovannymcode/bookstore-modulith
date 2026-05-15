/**
 * Módulo OPEN: todas sus clases son visibles para los demás módulos.
 *
 * Al ser OPEN, expone automáticamente todos sus sub-paquetes.
 * No necesita que los módulos clientes declaren dependencias explícitas.
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package com.geovannycode.bookstore.common;

import org.springframework.modulith.ApplicationModule;
