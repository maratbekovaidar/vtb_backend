package kz.kalybayevv.VtbNews.constants;

/**
 * Статус экспорта
 */
public enum ExportStatus {
    IN_QUEUE,       // Ожидает обработку
    PROCESSING,     // Обрабатывется
    EXPORT_ERROR,   // Ошибка
    SYSTEM_ERROR,   // Системная ошибка
    SUCCESS         // Успешно
}
