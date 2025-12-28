import { createFileRoute } from "@tanstack/react-router";
import {Button} from "@filey/ui";

export const Route = createFileRoute("/")({
    component: Index,
});

function Index() {
    return (
        <div className="flex min-h-screen items-center justify-center">
            <div className="text-center">
                <h1 className="text-4xl font-bold text-gray-900">Filey</h1>
                <p className="mt-2 text-gray-600">Self-hosted file server</p>
                <Button>This is some button</Button>
            </div>
        </div>
    );
}
