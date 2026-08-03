-- =============================================================================
-- Script de datos históricos de prueba para Baratito
-- Primero buscar productos antes de ejecutar la query (ej: shampoo)
-- =============================================================================

INSERT INTO precio_historico (id, producto_link, source, nombre, precio, fecha)
SELECT
    nextval('precio_historico_seq'),
    p.link,
    p.source,
    p.nombre,
    CASE
        WHEN gs.day_offset = 0  THEN p.precio
        ELSE p.precio * (1 - (gs.day_offset * 0.01) + (random() * 0.02))
    END,
    CURRENT_DATE - gs.day_offset
FROM producto_schema p
CROSS JOIN generate_series(0, 30) AS gs(day_offset)
WHERE
    p.disponibilidad = true
    AND p.id IN (
        SELECT id FROM producto_schema
        WHERE disponibilidad = true
        ORDER BY id
        LIMIT 200
    );

-- Verificar cuántos registros se insertaron:
SELECT
    nombre,
    source,
    COUNT(*) as cantidad_snapshots,
    MIN(precio) as precio_min,
    MAX(precio) as precio_max,
    MIN(fecha) as fecha_desde,
    MAX(fecha) as fecha_hasta
FROM precio_historico
GROUP BY nombre, source
ORDER BY nombre;

------------ con más variación de precio

-- 1. Limpiamos los datos anteriores de prueba para no mezclar
TRUNCATE TABLE precio_historico;

-- 2. Insertamos datos con mayor volatilidad
INSERT INTO precio_historico (id, producto_link, source, nombre, precio, fecha)
SELECT
    nextval('precio_historico_seq'),
    p.link,
    p.source,
    p.nombre,
    -- Generamos un precio base con variaciones más grandes (entre -10% y +10%)
    p.precio * (1 + ((random() - 0.5) * 0.2)),
    CURRENT_DATE - gs.day_offset
FROM producto_schema p
CROSS JOIN generate_series(0, 30) AS gs(day_offset)
WHERE p.disponibilidad = true
AND p.id IN (
    SELECT id FROM producto_schema
    WHERE disponibilidad = true
    ORDER BY random() -- Seleccionamos productos aleatorios
    LIMIT 50
);

-- 3. Verificamos la dispersión de precios
SELECT
    nombre,
    COUNT(*) as total_registros,
    MIN(precio) as precio_min,
    MAX(precio) as precio_max
FROM precio_historico
GROUP BY nombre
ORDER BY total_registros DESC
LIMIT 10;