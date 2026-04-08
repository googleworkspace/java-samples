Acerca del examen de secretos
Evite el uso fraudulento de los secretos mediante la detección automática de credenciales expuestas antes de que se puedan aprovechar.

En este artículo
Cuando las credenciales, como las claves de API y las contraseñas, se confirman en repositorios como secretos codificados de forma segura, se convierten en destinos para el acceso no autorizado. Secret scanning detecta automáticamente las fugas de credenciales para que pueda protegerlas antes de que se aprovechen.

Sugerencia

En cualquier momento, puede ejecutar una evaluación gratuita del código de su organización para los secretos filtrados.

Para generar un informe, abra la  Security and quality pestaña de la organización, muestra la  página Evaluaciones y, a continuación, haz clic en Examinar tu organización..

Cómo el escaneo de secretos protege tu código
          Secret scanning examina todo el historial de Git en todas las ramas del repositorio para obtener credenciales codificadas de forma segura, incluidas las claves de API, las contraseñas, los tokens y otros tipos de secreto conocidos. Esto le ayuda a identificar la expansión de secretos, la proliferación no controlada de credenciales entre repositorios, antes de que se convierta en un riesgo de seguridad. 
          GitHub también vuelve a examinar periódicamente los repositorios cuando se agregan nuevos tipos de secretos.

          GitHub también examina automáticamente:
Descripciones y comentarios sobre problemas
Títulos, descripciones y comentarios, en problemas históricos abiertos y cerrados
Títulos, descripciones y comentarios en pull requests
Títulos, descripciones y comentarios en GitHub Discussions
Wikis
Gists secretos
          Secret scanning alertas y corrección
Cuando secret scanning detecta una pérdida de credenciales, GitHub genera una alerta en la pestaña del  Security and quality repositorio con detalles sobre la credencial expuesta.

Cuando reciba una alerta, gire inmediatamente la credencial afectada para evitar el acceso no autorizado. Aunque también puede quitar secretos del historial de Git, esto requiere mucho tiempo y suele ser innecesario si ya ha revocado la credencial.

Integración de asociados
          GitHub se asocia con una gran variedad de proveedores de servicios para validar los secretos detectados. Cuando se detecta un secreto de asociado, se notifica al proveedor para que pueda tomar medidas, como revocar la credencial. Los secretos de los partners se reportan directamente al proveedor y no se muestran en las alertas de su repositorio. Para más información, consulta [AUTOTITLE](/code-security/secret-scanning/secret-scanning-partnership-program/secret-scanning-partner-program).
Personalización
Además de la detección predeterminada de secretos de socios y proveedores, puede expandir y personalizar secret scanning para adaptarse a sus necesidades.

        **Patrones que no son de proveedores.** Expanda la detección a secretos que no están vinculados a un proveedor de servicios específico, como claves privadas, cadenas de conexión y claves de API genéricas.
        **Patrones personalizados.** Defina sus propias expresiones regulares para detectar secretos específicos de la organización que no están cubiertos por patrones predeterminados.
        **Comprobaciones de validez.** Priorice la corrección comprobando si los secretos detectados siguen activos.
        **
        Escaneo secreto de Copilot.** Utiliza IA para detectar secretos no estructurados como contraseñas, o para generar expresiones regulares para patrones personalizados.
Acerca de las comprobaciones de validez
Las comprobaciones de validez le ayudan a priorizar qué secretos corregir primero comprobando si un secreto detectado sigue activo. Al habilitar comprobaciones de validez, secret scanning puede ponerse en contacto con el servicio emisor de la clave secreta para determinar si se ha revocado la credencial.

Las comprobaciones de validez son independientes secret scanning del programa de socios. Aunque los secretos de asociados se notifican automáticamente a los proveedores de servicios para la revocación, las comprobaciones de validez comprueban el estado de los secretos que administra en sus propias alertas. Para más información, consulta Acerca de las comprobaciones de validez.

¿Cómo puedo acceder a esta característica?
Secret scanning está disponible para los tipos de repositorio siguientes:

        **Repositorios públicos**: Secret scanning se ejecuta automáticamente y sin coste.
        **Repositorios privados e internos de la organización**: disponibles con [GitHub Secret Protection](/get-started/learning-about-github/about-github-advanced-security) habilitados en GitHub Team o GitHub Enterprise Cloud.
        **Repositorios propiedad del usuario**: disponibles en GitHub Enterprise Cloud con Enterprise Managed Users. Disponible en GitHub Enterprise Server cuando la empresa tiene [GitHub Secret Protection](/get-started/learning-about-github/about-github-advanced-security) habilitado.
Pasos siguientes
        **Si ha recibido una alerta**, consulte [AUTOTITLE](/code-security/secret-scanning/managing-alerts-from-secret-scanning) para obtener información sobre cómo revisar, resolver y corregir secretos expuestos.
        **Si va a proteger una organización**, consulte [AUTOTITLE](/code-security/how-tos/secure-at-scale/configure-organization-security/configure-specific-tools/assess-your-secret-risk) para determinar la exposición de su organización a secretos filtrados.
Lectura adicional
Para obtener una lista completa de los secretos y proveedores de servicios admitidos, consulte Patrones de análisis de secretos admitidos.
